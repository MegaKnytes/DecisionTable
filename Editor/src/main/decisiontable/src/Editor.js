import * as React from 'react';

import {SystemConfiguration, DecisionTable, VariableGroup, State, Rule} from "@/Rule.js"


export class Editor {
  //Class that stores the current state of the editor
  //Has attributes that keep the current Table and Rules
  static instance = new Editor();

  constructor() {
    this.createNewConfig();
    this.createNewTable(this.currentConfig);
    this.addNewVariableGroup("New Variable Group")
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





}