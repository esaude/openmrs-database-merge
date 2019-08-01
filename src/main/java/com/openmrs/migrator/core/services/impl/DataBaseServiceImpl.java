package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.config.ConfigurationStore;
import com.openmrs.migrator.core.services.CommandService;
import com.openmrs.migrator.core.services.DataBaseService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Database operations */
@Service
public class DataBaseServiceImpl implements DataBaseService {

  private final CommandService commandService;

  private final ConfigurationStore configurationStore;

  @Autowired
  public DataBaseServiceImpl(CommandService commandService, ConfigurationStore configurationStore) {
    this.commandService = commandService;
    this.configurationStore = configurationStore;
  }

  @Override
  public void importDatabaseFile(String databaseName, String fileName) {
    commandService.runCommand(
        "mysql",
        "-u" + configurationStore.getDatabaseUser(),
        "-p" + configurationStore.getDatabasePassword(),
        "-h" + configurationStore.getDatabasePassword(),
        "-e",
        String.format("use %s; source %s;", databaseName, fileName));
  }

  @Override
  public void createDatabase(String databaseName) {
    commandService.runCommand(
        "mysql",
        "-u" + configurationStore.getDatabaseUser(),
        "-p" + configurationStore.getDatabasePassword(),
        "-h" + configurationStore.getDatabaseHost(),
        "-e",
        String.format(
            "drop database if exists %s; create database %s;", databaseName, databaseName));
  }

  @Override
  public List<String> getDatabases(String password) throws IOException {
    Process p =
        Runtime.getRuntime()
            .exec(new String[] {"mysql", "-u", "root", "-p" + password, "-e", "show databases"});

    String result =
        new BufferedReader(new InputStreamReader(p.getInputStream()))
            .lines()
            .collect(Collectors.joining(","));

    return Arrays.asList(result.split(","));
  }
}
