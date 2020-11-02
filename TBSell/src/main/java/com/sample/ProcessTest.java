package com.sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

/**
 * This is a sample file to test a process.
 */
public class ProcessTest extends JbpmJUnitBaseTestCase {

	@Test
	public void testProcess() {
		RuntimeManager manager = createRuntimeManager("TBSell.bpmn");
		RuntimeEngine engine = getRuntimeEngine(null);
		KieSession ksession = engine.getKieSession();
		TaskService taskService = engine.getTaskService();
		
		   //变量绑定区
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("name", "Perkz");//用户的姓名
			params.put("e-mail", "66666@qq.com");//用户的e-mail 用来通知用户审核进度、审核结果等 也可以用电话号码代替这里就不用电话号了。
			params.put("state", 0);//用户当前的审核状态 0 表示未审核 ，1表示正在审核，2表示审核通过，3表示审核未通过,4代表审核通过后但是取消,5表示已放款.
			params.put("loanMoney",50000);//用户贷款金额
			params.put("credit",2);//用户信誉 1----2个等级 等级越高信誉越高 用来判断是否有资格借款，抽象出来的信誉，实际上应该用名下财产之类的数据进行审核这里进行了简化。
			params.put("Pre",1);//目的 1.预售 2.正常购买
			params.put("creditResult",0);//0 未审核 1通过 2拒绝
			params.put("loanPurposeResult",0);//0 未审核 1 通过 2 拒绝
			params.put("bankCardId","996996996");//银行卡号
		
		
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.TBSell",params);
		// check whether the process instance has completed successfully
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		assertNodeTriggered(processInstance.getId(), "Hello");
		
		//usertask and 信号运行区	
		HashMap<String, Object> results = new HashMap<String, Object>();
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		TaskSummary task = tasks.get(0);
		System.out.println("'mary' completing task " + task.getName() + ": " + task.getDescription());
		taskService.start(task.getId(), "mary");

		results.put("creditResult", 1);//客户信用通过   在这里可以通过 params中的money和credit等数据判断是否通过。
		taskService.complete(task.getId(), "mary", results);

		
		
		
		
		manager.disposeRuntimeEngine(engine);
		manager.close();
	}

}