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
	public ProcessTest() {
		super(true, true);
	}	
	@Test
	public void testProcess() {
		
		
		System.out.println("flow start!");
		System.out.println(" ");
		System.out.println(" ");
		//变量声明区
		RuntimeManager manager = createRuntimeManager("BankLoanWorkFlow.bpmn");
		RuntimeEngine engine = getRuntimeEngine(null);
		KieSession ksession = engine.getKieSession();
		TaskService taskService = engine.getTaskService();
		
	   //变量绑定区
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", "CaiXuKun");//用户的姓名
		params.put("e-mail", "166666@qq.com");//用户的e-mail 用来通知用户审核进度、审核结果等 也可以用电话号码代替这里就不用电话号了。
		params.put("state", 0);//用户当前的审核状态 0 表示未审核 ，1表示正在审核，2表示审核通过，3表示审核未通过,4代表审核通过后但是取消,5表示已放款.
		params.put("loanMoney",50000);//用户贷款金额
		params.put("credit",2);//用户信誉 1----2个等级 等级越高信誉越高 用来判断是否有资格借款，抽象出来的信誉，实际上应该用名下财产之类的数据进行审核这里进行了简化。
		params.put("loanPurpose",1);//贷款目的 1.个人其他贷款 2.企业其他贷款
		params.put("creditResult",0);//0 未审核 1通过 2拒绝
		params.put("loanPurposeResult",0);//0 未审核 1 通过 2 拒绝
		params.put("bankCardId","996996996");//银行卡号
//		params.put("OK",0);//0 未审核 1 通过 2 拒绝
		
		
		
		
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.bankLoanWorkFlow",params);
		
//		processInstance.signalEvent("abc",1);
		
		
		//usertask and 信号运行区	
		HashMap<String, Object> results = new HashMap<String, Object>();
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		TaskSummary task = tasks.get(0);
		System.out.println("'mary' completing task " + task.getName() + ": " + task.getDescription());
		taskService.start(task.getId(), "mary");

		results.put("creditResult", 1);//客户信用通过   在这里可以通过 params中的money和credit等数据判断是否通过。
		taskService.complete(task.getId(), "mary", results);
		 
		
		
		HashMap<String, Object> results2 = new HashMap<String, Object>();
		List<TaskSummary> tasks2 = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		TaskSummary task2 = tasks2.get(0);
		System.out.println("'john' completing task " + task2.getName() + ": " + task2.getDescription());
		taskService.start(task2.getId(), "john");
		results2.put("loanPurposeResult", 1);//贷款目的通过 在这里可以通过 params中的money和loanPurpose等数据判断是否通过。
		taskService.complete(task2.getId(), "john", results2);
		
		
		
		
		HashMap<String, Object> results3 = new HashMap<String, Object>();
		List<TaskSummary> tasks3 = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		TaskSummary task3 = tasks3.get(0);
		System.out.println("'krisv' completing task " + task3.getName() + ": " + task3.getDescription());
		taskService.start(task3.getId(), "krisv");
		results3.put("prepare", 0);//贷款目的通过 在这里可以通过 params中的数据判断是否通过。 1表示准备完成 0表示准备未完成  
		//为0第一次不通过
		taskService.complete(task3.getId(), "krisv", results3);
		
		
		tasks3 = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task3 = tasks3.get(0);
		System.out.println("'krisv' completing task " + task3.getName() + ": " + task3.getDescription());
		taskService.start(task3.getId(), "krisv");
		results3.replace("prepare", 0);
		//为0第二次不通过 
		taskService.complete(task3.getId(), "krisv", results3);
		
		tasks3 = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task3 = tasks3.get(0);
		System.out.println("'krisv' completing task " + task3.getName() + ": " + task3.getDescription());
		taskService.start(task3.getId(), "krisv");
		results3.replace("prepare", 1);
		//为1第三次通过 
		taskService.complete(task3.getId(), "krisv", results3);
		
		
		ksession.signalEvent("Confirmation",1,processInstance.getId());
		try{ 
			Thread.currentThread().sleep(3000);
			}catch(InterruptedException e)
			{
			}
		
		
		
		HashMap<String, Object> results4 = new HashMap<String, Object>();
		List<TaskSummary> tasks4 = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		TaskSummary task4 = tasks4.get(0);
		System.out.println("'mary' completing task " + task4.getName() + ": " + task4.getDescription());
		taskService.start(task4.getId(), "mary");

		results.put("state", 5);//客户信用通过   在这里可以通过 params中的money和credit等数据判断是否通过。
		taskService.complete(task4.getId(), "mary", results4);
		
		
		
		System.out.println(" ");
		System.out.println(" ");
		System.out.println("flow end!");
		
		
		
		
		
	
		
		
		
		
		
		
		
		
		
		// check whether the process instance has completed successfully 系统自检区？
//		assertProcessInstanceCompleted(processInstance.getId(), ksession);
//		assertNodeTriggered(processInstance.getId(), "Hello");
		
		manager.disposeRuntimeEngine(engine);
		manager.close();
	}

}