package com.openmrs.migrator.core.services;

import com.openmrs.migrator.core.exception.CommandExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommandServiceTest {

  @Autowired private CommandService commandService;

  @Test
  public void shouldExecuteLsCommand() {
    try {
      commandService.runCommand("ls", "-ltr");
    } catch (CommandExecutionException e) {
      Assert.fail("Should execute successfully");
    }
  }

  @Test(expected = CommandExecutionException.class)
  public void shouldRaiseExceptionIfInvalidCommand() {
    commandService.runCommand("unexistingcommand");
  }
}
