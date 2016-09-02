package com.github.andlaz.mesos.ffmpeg.scheduler;

import com.github.andlaz.mesos.ffmpeg.service.ConfigService;
import com.google.protobuf.ByteString;
import org.apache.mesos.Protos;
import org.apache.mesos.Protos.*;
import org.apache.mesos.Scheduler;
import org.apache.mesos.SchedulerDriver;

import java.io.File;
import java.util.*;

public class FFMPEGScheduler implements Scheduler {

  private int launchedTasks = 0;
  private int finishedTasks = 0;
  private List<String> connectionQueue;
  private List<String> completedConnectionQueue;
  private Map<String, Set<String>> edgeList;
  private Map<String, String> urlToFileNameMap;




  @Override
  public void registered(SchedulerDriver driver, FrameworkID frameworkId, MasterInfo masterInfo) {
    System.out.println("Registered! ID = " + frameworkId.getValue());
  }

  @Override
  public void reregistered(SchedulerDriver driver, MasterInfo masterInfo) {
  }

  @Override
  public void disconnected(SchedulerDriver driver) {
  }

  @Override
  public void resourceOffers(SchedulerDriver driver, List<Offer> offers) {

      // docker image info
      Protos.ContainerInfo.DockerInfo.Builder dockerInfoBuilder = Protos.ContainerInfo.DockerInfo.newBuilder();
      dockerInfoBuilder.setImage(ConfigService.getImageName());
      dockerInfoBuilder.setNetwork(Protos.ContainerInfo.DockerInfo.Network.BRIDGE);

      // container info
      Protos.ContainerInfo.Builder containerInfoBuilder = Protos.ContainerInfo.newBuilder();
      containerInfoBuilder.setType(Protos.ContainerInfo.Type.DOCKER);
      containerInfoBuilder.setDocker(dockerInfoBuilder.build());


      for (Offer offer : offers) {
          List<TaskInfo> tasks = new ArrayList<TaskInfo>();
          if ( !connectionQueue.isEmpty()) {
              TaskID taskId = TaskID.newBuilder().setValue(Integer.toString(launchedTasks++)).build();

        String urlData = connectionQueue.get(0);

              Protos.TaskInfo task = Protos.TaskInfo.newBuilder()
                      .setName("task " + taskId.getValue())
                      .setTaskId(taskId)
                      .setSlaveId(offer.getSlaveId())
                      .addResources(Protos.Resource.newBuilder()
                              .setName("cpus")
                              .setType(Protos.Value.Type.SCALAR)
                              .setScalar(Protos.Value.Scalar.newBuilder().setValue(1)))
                      .addResources(Protos.Resource.newBuilder()
                              .setName("mem")
                              .setType(Protos.Value.Type.SCALAR)
                              .setScalar(Protos.Value.Scalar.newBuilder().setValue(128)))
                      .setContainer(containerInfoBuilder)
                      .build();

              tasks.add(task);

        taskId = TaskID.newBuilder().setValue(Integer.toString(launchedTasks++)).build();

        System.out.println("Launching task " + taskId.getValue() + " with input: " + urlData);

        tasks.add(task);
        // Dequeue and update completed list
        completedConnectionQueue.add(urlData);
        connectionQueue.remove(0);

      }
      driver.launchTasks(offer.getId(), tasks);
    }
  }

  @Override
  public void offerRescinded(SchedulerDriver driver, OfferID offerId) {
  }

  @Override
  public void statusUpdate(SchedulerDriver driver, TaskStatus status) {

      if (status.getState() == TaskState.TASK_FINISHED || status.getState() == TaskState.TASK_LOST) {
          System.out.println("Status update: task " + status.getTaskId().getValue()
                  + " has completed with state " + status.getState());
          finishedTasks++;
          System.out.println("Finished tasks: " + finishedTasks);
          // if task is running then
          // unblock the thread
          // so that we can start streaming
          if (status.getState() == TaskState.TASK_RUNNING) {
              // check for slave status
          }
      }
  }

  @Override
  public void frameworkMessage(SchedulerDriver driver, ExecutorID executorId, SlaveID slaveId,
      byte[] data) {

  }

  @Override
  public void slaveLost(SchedulerDriver driver, SlaveID slaveId) {
  }

  @Override
  public void executorLost(SchedulerDriver driver, ExecutorID executorId, SlaveID slaveId,
      int status) {
  }

  @Override
  public void error(SchedulerDriver driver, String message) {
    System.out.println("Error: " + message);
  }

}
