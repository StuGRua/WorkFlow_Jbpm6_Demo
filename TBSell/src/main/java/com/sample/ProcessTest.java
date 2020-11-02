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
		
		   //��������
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("name", "Perkz");//�û�������
			params.put("e-mail", "66666@qq.com");//�û���e-mail ����֪ͨ�û���˽��ȡ���˽���� Ҳ�����õ绰�����������Ͳ��õ绰���ˡ�
			params.put("state", 0);//�û���ǰ�����״̬ 0 ��ʾδ��� ��1��ʾ������ˣ�2��ʾ���ͨ����3��ʾ���δͨ��,4�������ͨ������ȡ��,5��ʾ�ѷſ�.
			params.put("loanMoney",50000);//�û�������
			params.put("credit",2);//�û����� 1----2���ȼ� �ȼ�Խ������Խ�� �����ж��Ƿ����ʸ�����������������ʵ����Ӧ�������²Ʋ�֮������ݽ��������������˼򻯡�
			params.put("Pre",1);//Ŀ�� 1.Ԥ�� 2.��������
			params.put("creditResult",0);//0 δ��� 1ͨ�� 2�ܾ�
			params.put("loanPurposeResult",0);//0 δ��� 1 ͨ�� 2 �ܾ�
			params.put("bankCardId","996996996");//���п���
		
		
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.TBSell",params);
		// check whether the process instance has completed successfully
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		assertNodeTriggered(processInstance.getId(), "Hello");
		
		//usertask and �ź�������	
		HashMap<String, Object> results = new HashMap<String, Object>();
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		TaskSummary task = tasks.get(0);
		System.out.println("'mary' completing task " + task.getName() + ": " + task.getDescription());
		taskService.start(task.getId(), "mary");

		results.put("creditResult", 1);//�ͻ�����ͨ��   ���������ͨ�� params�е�money��credit�������ж��Ƿ�ͨ����
		taskService.complete(task.getId(), "mary", results);

		
		
		
		
		manager.disposeRuntimeEngine(engine);
		manager.close();
	}

}