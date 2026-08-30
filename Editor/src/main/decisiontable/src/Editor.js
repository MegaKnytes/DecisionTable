import * as React from 'react';

import {SystemConfiguration, DecisionTable, VariableGroup, State, Rule} from "@/Rule.js"


export class Editor {
  //Class that stores the current state of the editor
  //Has attributes that keep the current Table and Rules
  static instance = new Editor();

  constructor() {
    this.createNewConfig();
    this.createNewTable(this.currentConfig);

    this.addNewVariableGroup("New Variable Group");
    this.addNewState("New State");
    this.addNewRule("New Rule");
  }

  //Functions to create new blank configs/tables
  createNewConfig() {
    this.currentConfig = new SystemConfiguration("New Config",true,[],[],[]);
  }
  createNewTable() {
    this.currentTable = new DecisionTable("New DTP",true,"TELEOP",this.currentConfig,"",[]);
  }

  //Functions to handle rules list
  addNewRule(name) {
    this.currentTable.rules.push(new Rule(name,false,0,[],[]));
  }
  getRuleList() {
    return(this.currentTable.rules);
  }
  getRule(index) {
    return(this.currentTable.rules[index]);
  }
  setRuleConditions(index,conditions) {
    this.currentTable.rules[index].conditions = conditions;
  }
  setRuleActions(index,actions) {
    this.currentTable.rules[index].actions = actions;
  }
  setRuleName(index, ruleName) {
    this.currentTable.rules[index].name = ruleName;
  }

  //Functions to handle states list
  addNewState(name) {
    this.currentConfig.states.push(new State(name,[]));
  }
  getStateList() {
    return(this.currentConfig.states);
  }
  getState(index) {
    return(this.currentConfig.states[index]);
  }
  setStateValues(index,values) {
    this.currentConfig.states[index].values = values;
  }
  setStateName(index, stateName) {
    this.currentConfig.states[index].name = stateName;
  }

  //Functions to handle variable groups list
  addNewVariableGroup(name) {
    this.currentConfig.variableGroups.push(new VariableGroup(name,[]));
  }
  getVariableGroupList() {
    return(this.currentConfig.variableGroups);
  }
  getVariableGroup(index) {
    return(this.currentConfig.variableGroups[index]);
  }
  setVariableGroupVariables(index,variables) {
    this.currentConfig.variableGroups[index].variables = variables;
  }
  setVariableGroupName(index, groupName) {
    this.currentConfig.variableGroups[index].name = groupName;
  }
}