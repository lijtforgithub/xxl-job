package com.xxl.job.executor.test;

import com.youma.xxljob.extend.biz.enums.PlanEnum;
import com.youma.xxljob.extend.biz.model.AssignPlanJobParam;
import com.youma.xxljob.extend.biz.model.CancelJobParam;
import com.youma.xxljob.extend.biz.model.CyclePlanJobParam;
import com.youma.xxljob.extend.biz.model.builder.PlanJobBuilder;
import com.youma.xxljob.extend.helper.XxlJobExtendHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XxlJobExecutorExampleBootApplicationTests {

	@Test
	public void testAssign() {
		AssignPlanJobParam planJobParam = PlanJobBuilder.assignPlanJobBuilder()
				.withJobHandler("demoJobHandler")
				.withDateTime("2023-03-15 13:00:00", "2023-03-16 02:00:00")
				.withHandlerParam("回调参数")
				.build();
		XxlJobExtendHelper.buildClient().addPlan(planJobParam);
	}

	@Test
	public void testDaily() {
		CyclePlanJobParam planJobParam = PlanJobBuilder.cyclePlanJobBuilder()
				.withPlanType(PlanEnum.DAILY)
				.withJobHandler("demoJobHandler")
				.withCycleExeTime("08:00:00")
				.withExeOnce(true)
//				.withJobKey("3")
//				.withCycleInterval(2)
				.withHandlerParam("每日任务")
				.build();
		XxlJobExtendHelper.buildClient().addPlan(planJobParam);
	}

	@Test
	public void testWeek() {
		CyclePlanJobParam planJobParam = PlanJobBuilder.cyclePlanJobBuilder()
				.withPlanType(PlanEnum.WEEK)
				.withJobHandler("demoJobHandler")
				.withCycleExeTime("14:00:00")
				.withCycleInterval(2)
				.withPlanOption(1)
				.withHandlerParam(PlanEnum.WEEK.getDesc())
				.build();
		XxlJobExtendHelper.buildClient().addPlan(planJobParam);
	}

	@Test
	public void testMonth() {
		CyclePlanJobParam planJobParam = PlanJobBuilder.cyclePlanJobBuilder()
				.withPlanType(PlanEnum.MONTH)
				.withJobHandler("demoJobHandler")
				.withCycleExeTime("20:00:00")
				.withCycleInterval(2)
				.withPlanOption(5, 1)
				.build();
		XxlJobExtendHelper.buildClient().addPlan(planJobParam);
	}

	@Test
	public void testSeason() {
		CyclePlanJobParam planJobParam = PlanJobBuilder.cyclePlanJobBuilder()
				.withPlanType(PlanEnum.SEASON)
				.withJobHandler("demoJobHandler")
				.withCycleExeTime("20:00:00")
				.withPlanOption(14)
				.withPlanOption(PlanEnum.TWO_STAGE_OPTION + 3)
				.withPlanOption(PlanEnum.TWO_STAGE_OPTION + 2)
				.build();
		XxlJobExtendHelper.buildClient().addPlan(planJobParam);
	}

	@Test
	public void testCancel() {
		XxlJobExtendHelper.buildClient().cancelPlanById("81");
		CancelJobParam param = new CancelJobParam();
//		param.setJobHandler("demoJobHandler");
//		param.setJobKey("4e704327-b965-4048-8d6c-fb99f1fb40c6");
//		XxlJobExtendHelper.buildClient().cancelByJobKey(param);
	}

}