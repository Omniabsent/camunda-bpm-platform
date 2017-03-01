package org.camunda.bpm.getstarted.signalreceiveloop.test;


import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.complete;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.processInstanceQuery;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.task;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class InMemoryH2Test {

	@Rule
	  public ProcessEngineRule processEngineRule = new ProcessEngineRule();

	  @Test
	  @Deployment(resources = {"signal-receive-loop.bpmn"})
	  public void ruleUsageExample() {
	    RuntimeService runtimeService = processEngineRule.getRuntimeService();
	    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("signal-receive-loop");
	    String prIID = processInstance.getId();
	    System.out.println("Instance running: " + processInstance.getId());
	    System.out.println("current activity: " + runtimeService.getActiveActivityIds(processInstance.getId()).get(0));
	    
	    // signal before listening
	    System.out.println("sending signal before listening");
	    runtimeService
	      .createSignalEvent("Signal_3min-loop")
	      .send();
	    
	    
	    System.out.println("do complete() to go to event receiver");
	    System.out.println("Signal reception should be replayed from buffer.");
	    complete(task(processInstance));
	    
	    System.out.println("current activity (should be after receiver): " + runtimeService.getActiveActivityIds(processInstance.getId()).get(0));
	    
	    // trigger timer and do another round
//	    ManagementService mgmtSvc = processEngineRule.getManagementService();
//	    List<Job> timerJobs = mgmtSvc.createJobQuery().processInstanceId(prIID).timers().active().list();
//	    timerJobs.get(0);
	    
	    // Then it should be active
	    assertThat(processInstance).isActive();
	    // And it should be the only instance
	    assertThat(processInstanceQuery().count()).isEqualTo(1);
	    // And there should exist just a single task within that process instance
	    assertThat(task(processInstance)).isNotNull();
	    
	    System.out.println("activity instance:");
	    System.out.println(runtimeService.getActivityInstance(prIID));
	    
	    
	    // When we complete that task
	    System.out.println("execute complete()");
	    complete(task(processInstance));
	    System.out.println("current task: " + task(processInstance));
	    System.out.println("current activity 0: " + runtimeService.getActiveActivityIds(processInstance.getId()).get(0));
	    System.out.println("execute complete()");
	    complete(task(processInstance));
	    System.out.println("current task: " + task(processInstance));
	    // Then the process instance should be ended
	    assertThat(processInstance).isEnded();
	    System.out.println("Process finished as expected");
	  }

}

