package me.dolphago.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StepNextConditionalJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job stepNextConditionalJob() {
        return jobBuilderFactory.get("stepNextConditionalJob")
                                .start(conditionalJobStep1())// start대신 flow를 사용해도 된다.
                                .on("FAILED")// Step1이 FAILED일 경우
                                .to(conditionalJobStep3())// Step3으로 이동한다.
                                .on("*")// Step3의 결과 관계 없이
                                .end()//Step3으로 이동하면 Flow가 종료된다.
                                .from(conditionalJobStep1())//Step1로부터
                                .on("*")//FAILED 외의 모든 경우
                                .to(conditionalJobStep3())// Step 2로 이동한다.
                                .next(conditionalJobStep2())//Step 2가 정상 종료하면, Step3으로 이동한다.
                                .on("*")//Step 3의 결과와 관계 없이
                                .end()
                                .end()//Job 종료
                                .build();
    }

    @Bean
    public Step conditionalJobStep1() {
        return stepBuilderFactory.get("step1")
                                 .tasklet((contribution, chunkContext) -> {
                                     log.info(">>>>> This is stepNextConditionalJob Step1");
                                     return RepeatStatus.FINISHED;
                                 }).build();
    }

    @Bean
    public Step conditionalJobStep2() {
        return stepBuilderFactory.get("conditionalJobStep2")
                                 .tasklet((contribution, chunkContext) -> {
                                     log.info(">>>>> This is stepNextConditionalJob Step2");
                                     return RepeatStatus.FINISHED;
                                 }).build();
    }

    @Bean
    public Step conditionalJobStep3() {
        return stepBuilderFactory.get("conditionalJobStep3")
                                 .tasklet((contribution, chunkContext) -> {
                                     log.info(">>>>> This is stepNextConditionalJob Step3");
                                     return RepeatStatus.FINISHED;
                                 }).build();
    }

}

