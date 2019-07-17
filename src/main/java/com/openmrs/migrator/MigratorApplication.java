package com.openmrs.migrator;

import com.openmrs.migrator.core.services.BootstrapService;
import com.openmrs.migrator.core.services.PDIService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.pentaho.di.core.exception.KettleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MigratorApplication implements CommandLineRunner {

  private static Logger LOG = LoggerFactory.getLogger(MigratorApplication.class);

  private final PDIService pdiService;

  private FileIOUtilities fileIOUtilities;

  private String[] jobs = {"pdiresources/jobs/merge-patient-job.kjb"};

  private List<String> dirList = Arrays.asList("input/", "output/", "config/", "pdiresources/");

  private Path settingsProperties = Paths.get("settings.properties");

  private BootstrapService bootstrapService;

  @Autowired
  public MigratorApplication(
      PDIService pdiService, FileIOUtilities fileIOUtilities, BootstrapService bootstrapService) {
    this.pdiService = pdiService;
    this.fileIOUtilities = fileIOUtilities;
    this.bootstrapService = bootstrapService;
  }

  public static void main(String[] args) {
    SpringApplication.run(MigratorApplication.class, args);
  }

  @Override
  public void run(String... args) throws IOException {
    LOG.info("EXECUTING : command line runner");

    for (int i = 0; i < args.length; ++i) {
      LOG.info("args[{}]: {}", i, args[i]);
    }

    if (args.length > 0 && "setup".equals(args[0])) {

      executeSetupCommand();
    }

    if (args.length > 0 && "run".equals(args[0])) {
      runAllJobs();
    } else {
      System.out.println("Usage: migrator run");
    }
  }

  private void runAllJobs() throws IOException {
    try {
      for (String t : jobs) {

        InputStream xml = fileIOUtilities.getResourceAsStream(t);
        pdiService.runJob(xml);
      }
    } catch (KettleException e) {
      // Do nothing kettle prints stack trace
    }
  }

  private void executeSetupCommand() throws IOException {

    bootstrapService.createDirectoryStructure(dirList, settingsProperties);
    bootstrapService.populateDefaultResouce();
  }
}
