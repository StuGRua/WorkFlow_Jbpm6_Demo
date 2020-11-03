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
 * This is a sample test of the evaluation process.
 */
public class ProcessTest extends JbpmJUnitBaseTestCase {
	
	public ProcessTest() {
		super(true, true);
	}	

	@Test
	public void testTBSellProcess() {
		
		RuntimeManager manager = createRuntimeManager("TBSell.bpmn");
		RuntimeEngine engine = getRuntimeEngine(null);
		KieSession ksession = engine.getKieSession();
		TaskService taskService = engine.getTaskService();
		
		//start a new process instance
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user", "krisv");
		params.put("Pre", 0);
		params.put("YN", 1);
		params.put("state", 0);
		params.put("money", "$300");
		ProcessInstance processInstance = 
			ksession.startProcess("com.sample.TBSell", params);
		
		//Fill
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		TaskSummary task = tasks.get(0);
		System.out.println("krisv"+" is doing task " + task.getName() + ": " + task.getDescription());
		taskService.start(task.getId(), "krisv");
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("Pre", 1);//0 or 1
		taskService.complete(task.getId(), "krisv", results);
		
		 //YN
		tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task = tasks.get(0);
		System.out.println("krisv"+" is doing task " + task.getName() + ": " + task.getDescription());
		taskService.start(task.getId(), "krisv");
		results = new HashMap<String, Object>();
		results.put("YN", 1);
		taskService.complete(task.getId(), "krisv", results);
		
		//Ali
		tasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		task = tasks.get(0);
		System.out.println("mary"+" is doing task " + task.getName() + ": " + task.getDescription());
		taskService.start(task.getId(), "mary");
		results = new HashMap<String, Object>();
		results.put("state", 1);
		taskService.complete(task.getId(), "mary", results);		
		
		//Send
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("john"+" is doing task " + task.getName() + ": " + task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		results.put("state", 2);
		taskService.complete(task.getId(), "john", results);		
		try{ 
			Thread.currentThread().sleep(6000);
			}catch(InterruptedException e)
			{
			}
		ksession.signalEvent("Confirmation",1,processInstance.getId());
		//Receive
		tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task = tasks.get(0);
		System.out.println("krisv"+" is doing task " + task.getName() + ": " + task.getDescription());
		taskService.start(task.getId(), "krisv");
		results = new HashMap<String, Object>();
		results.put("state", 3);
		taskService.complete(task.getId(), "krisv", results);
		
		


		
		
		System.out.println("Process is completed");
		
		manager.disposeRuntimeEngine(engine);
		manager.close();
	}

	
}
