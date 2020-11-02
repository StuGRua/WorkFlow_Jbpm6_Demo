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
		//����������
		RuntimeManager manager = createRuntimeManager("BankLoanWorkFlow.bpmn");
		RuntimeEngine engine = getRuntimeEngine(null);
		KieSession ksession = engine.getKieSession();
		TaskService taskService = engine.getTaskService();
		
	   //��������
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", "CaiXuKun");//�û�������
		params.put("e-mail", "166666@qq.com");//�û���e-mail ����֪ͨ�û���˽��ȡ���˽���� Ҳ�����õ绰�����������Ͳ��õ绰���ˡ�
		params.put("state", 0);//�û���ǰ�����״̬ 0 ��ʾδ��� ��1��ʾ������ˣ�2��ʾ���ͨ����3��ʾ���δͨ��,4�������ͨ������ȡ��,5��ʾ�ѷſ�.
		params.put("loanMoney",50000);//�û�������
		params.put("credit",2);//�û����� 1----2���ȼ� �ȼ�Խ������Խ�� �����ж��Ƿ����ʸ�����������������ʵ����Ӧ�������²Ʋ�֮������ݽ��������������˼򻯡�
		params.put("loanPurpose",1);//����Ŀ�� 1.������������ 2.��ҵ��������
		params.put("creditResult",0);//0 δ��� 1ͨ�� 2�ܾ�
		params.put("loanPurposeResult",0);//0 δ��� 1 ͨ�� 2 �ܾ�
		params.put("bankCardId","996996996");//���п���
//		params.put("OK",0);//0 δ��� 1 ͨ�� 2 �ܾ�
		
		
		
		
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.bankLoanWorkFlow",params);
		
//		processInstance.signalEvent("abc",1);
		
		
		//usertask and �ź�������	
		HashMap<String, Object> results = new HashMap<String, Object>();
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		TaskSummary task = tasks.get(0);
		System.out.println("'mary' completing task " + task.getName() + ": " + task.getDescription());
		taskService.start(task.getId(), "mary");

		results.put("creditResult", 1);//�ͻ�����ͨ��   ���������ͨ�� params�е�money��credit�������ж��Ƿ�ͨ����
		taskService.complete(task.getId(), "mary", results);
		 
		
		
		HashMap<String, Object> results2 = new HashMap<String, Object>();
		List<TaskSummary> tasks2 = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		TaskSummary task2 = tasks2.get(0);
		System.out.println("'john' completing task " + task2.getName() + ": " + task2.getDescription());
		taskService.start(task2.getId(), "john");
		results2.put("loanPurposeResult", 1);//����Ŀ��ͨ�� ���������ͨ�� params�е�money��loanPurpose�������ж��Ƿ�ͨ����
		taskService.complete(task2.getId(), "john", results2);
		
		
		
		
		HashMap<String, Object> results3 = new HashMap<String, Object>();
		List<TaskSummary> tasks3 = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		TaskSummary task3 = tasks3.get(0);
		System.out.println("'krisv' completing task " + task3.getName() + ": " + task3.getDescription());
		taskService.start(task3.getId(), "krisv");
		results3.put("prepare", 0);//����Ŀ��ͨ�� ���������ͨ�� params�е������ж��Ƿ�ͨ���� 1��ʾ׼����� 0��ʾ׼��δ���  
		//Ϊ0��һ�β�ͨ��
		taskService.complete(task3.getId(), "krisv", results3);
		
		
		tasks3 = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task3 = tasks3.get(0);
		System.out.println("'krisv' completing task " + task3.getName() + ": " + task3.getDescription());
		taskService.start(task3.getId(), "krisv");
		results3.replace("prepare", 0);
		//Ϊ0�ڶ��β�ͨ�� 
		taskService.complete(task3.getId(), "krisv", results3);
		
		tasks3 = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task3 = tasks3.get(0);
		System.out.println("'krisv' completing task " + task3.getName() + ": " + task3.getDescription());
		taskService.start(task3.getId(), "krisv");
		results3.replace("prepare", 1);
		//Ϊ1������ͨ�� 
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

		results.put("state", 5);//�ͻ�����ͨ��   ���������ͨ�� params�е�money��credit�������ж��Ƿ�ͨ����
		taskService.complete(task4.getId(), "mary", results4);
		
		
		
		System.out.println(" ");
		System.out.println(" ");
		System.out.println("flow end!");
		
		
		
		
		
	
		
		
		
		
		
		
		
		
		
		// check whether the process instance has completed successfully ϵͳ�Լ�����
//		assertProcessInstanceCompleted(processInstance.getId(), ksession);
//		assertNodeTriggered(processInstance.getId(), "Hello");
		
		manager.disposeRuntimeEngine(engine);
		manager.close();
	}

}