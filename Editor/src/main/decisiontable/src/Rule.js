/* This file contains the classes used to store the data before being output to xml
 * Each class stores data, and has functions that check whether it is valid.
 * In the future each class will also have a toXML function for output.
 */

import * as React from 'react';

//Represents the <Rule> XML tag
export class Rule {
  constructor(name,debug,priority,conditions,actions) {
    //Attributes
    this.name = name;
    this.debug = debug;
    this.priority = priority;

    this.conditions = conditions; //Represents the <Condition> tag
    this.actions = actions; //Represents the <Action> tag
  }

  //Checks whether the rule is valid by iterating over and checking all conditions and actions for validity
  checkValidity(systemConfig) {
    let isValid = true;
    for(let condition=0; condition < this.conditions.length; condition++) {
      isValid = isValid && this.conditions[condition].checkValidity(systemConfig);
    }
    return(isValid);
  }
}

//Represents the items in the <Condition> XML tag, e.g. <Equals> and <NotEquals>
export class Condition {
  constructor(target,condition,value) {
    //Tag name (because the tag changes based on what condition it is)
    this.condition = condition;

    //Attributes
    this.target = target;
    this.value = value;
  }

  //Checks whether target are valid by checking with decision
  checkValidity(systemConfig) {
    return(systemConfig.checkTargetValidity(this.target));
  }
}

//Represents the <Set> tags in the <Action> XML tag
export class Action {
  constructor(device,value) {
    this.device = device;
    this.value = value;
  }
}

//Classes to store variables, states, and devices

//Represents the <Device> tag
export class Device {
  constructor(name,driver,parameters) {
    //Attributes
    this.name = name;
    this.driver = driver;

    //Children
    this.parameters = parameters;

    //This is helper values to check whether names are valid
    //This is just a testing version; for the final one, it should rely on the driver to know which parameters can be accessed
    this.paramName = "param:" + name;
    this.previousName= "prev:" + name;
  }
  checkTarget(nameToCheck) {
    return(nameToCheck == this.paramName || nameToCheck == this.previousName);
  }
}

//Represents the <VariableGroup> XML tag
export class VariableGroup {
  constructor(name,variables) {
    //Attributes
    this.name = name;

    //Children
    this.variables = variables;
  }

  checkTarget(nameToCheck) {
    let isValid = false;
    for(let variable = 0; variable < this.variables.length; variable++) {
      isValid = this.variables[variable].checkTarget(nameToCheck) || isValid;
    }
    return(isValid);
  }
}

//Represents the <Variable> XML tag
export class Variable {
  constructor(name,type,value,description) {
    //Attributes
    this.name = name;
    this.varname = "var:" + name;
    this.type = type; //Should eventually use this to check value validity
    this.value = value;
    this.description = description;
  }

  checkTarget(nameToCheck) {
    return(nameToCheck == this.varname);
  }
}

//Represents the <DecisionTable> tag
export class DecisionTable {
  constructor(name,enabled,type,config,transitionTarget,rules) {
    //Attributes
    this.name = name;
    this.enabled = enabled;
    this.type = type;
    this.config = config;
    this.transitionTarget = transitionTarget;

    //Children
    this.rules = rules;
  }

  checkValidity(systemConfig) {
    let isValid = true;
    for(let rule=0; rule < this.rules.length; rule++) {
      isValid = isValid && this.rules[rule].checkValidity(systemConfig);
    }
    return(isValid);
  }
}

//Represents the <SystemConfiguration> tag
export class SystemConfiguration {
  constructor(name,enabled,states,devices,variableGroups) {
    //Attributes
    this.name = name;
    this.enabled = enabled;

    //Children
    this.states = states;
    this.devices = devices;
    this.variableGroups = variableGroups;
  }

  checkTargetValidity(targetName) {
    //Checks whether a given target is valid
    let isValid = false;

    //Loop through devices to check if it matches a device target
    for(let device = 0; device < this.devices.length; device++) {
      isValid = this.devices[device].checkTarget(targetName) || isValid;
    }

    //Loop through variables to check if it matches a variable name
    for(let variable = 0; variable < this.variableGroups.length; variable++) {
      isValid = this.variableGroups[variable].checkTarget(targetName) || isValid;
    }

    return(isValid);
  }
}

export class State {
  constructor(name,values) {
    //Attributes
    this.name = name;

    //Children
    this.values = values;
  }
}

export class StateValue {
  constructor(name) {
    //Attributes
    this.name = name;
  }
}

const DEBUGGING = true;
if(DEBUGGING) {
  //Unit test setup
  //Define SystemConfiguration
  let devices = [new Device("Motor1","Motor",[]),new Device("Motor2","Motor",[])];
  let variables = [new VariableGroup("Position Coordinates",[new Variable("X"),new Variable("Y")])];
  let states = [new State("Bucket",[new StateValue("DUMP"),new StateValue("WAIT"),new StateValue("INTAKE")])];
  let twoMotorConfig = new SystemConfiguration("Eleanor",true,states,devices,variables);

  //Should be valid rule, passing assert
  console.log("Checking 1...");
  let r1condition1 = new Condition("var:X", "Equals", 0);
  let r1condition2 = new Condition("param:Motor1", "Equals", 0);
  let r1action = new Action("param:Motor1", 4);
  let rule1 = new Rule("Open Claw", false, 0, [r1condition1,r1condition2], [r1action]);
  console.assert(rule1.checkValidity(twoMotorConfig));

  //Should be invalid rule, thus negating should pass assert
  console.log("Checking 2...");
  let r2condition1 = new Condition("var:Xarg", 0, 0); //Nonexistant var name should be counted invalid
  let r2condition2 = new Condition("param:Motor1", 0, 0);
  let r2action = new Action("param:Motor1", 4);
  let rule2 = new Rule("Open Claw", false, 0, [r2condition1,r2condition2], [r2action]);
  console.assert(!rule2.checkValidity(twoMotorConfig));

  //Entire table should not be valid because of rule two, thus negating should pass assert
  let decisionTable = new DecisionTable("DTP #1",true,"TELEOP",twoMotorConfig,"",[rule1,rule2]);
  console.log("Checking table...");
  console.assert(!decisionTable.checkValidity(twoMotorConfig));

  console.log("Checks complete. Any asserts failing mean something is broken.");
}