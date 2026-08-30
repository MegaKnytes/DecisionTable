'use client'

import * as React from 'react'
import {AppBar, Box, Button, IconButton, Toolbar, Typography, TextField, ToggleButtonGroup, ToggleButton} from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';

// Button group that holds the comparison operators for the DTP rules
export default function RuleComparison({index,initialValue, handleConditionSelectorChange}) {
  const [comparison, setComparison] = React.useState(initialValue);
  console.log(initialValue);

  const handleChange = (event, newCondition) => {
    setComparison(newCondition);
    handleConditionSelectorChange(newCondition,index);

    console.log(newCondition);
  };

  return (
    <ToggleButtonGroup color="primary" value={comparison} exclusive onChange={handleChange} aria-label="Platform" sx={{ flexGrow: 1}}>
      <ToggleButton value="eql" sx={{ flexGrow: 1}}>=</ToggleButton>
      <ToggleButton value="neq" sx={{ flexGrow: 1}}>!=</ToggleButton>
      <ToggleButton value="ltn" sx={{ flexGrow: 1}}>{'<'}</ToggleButton>
      <ToggleButton value="gtr" sx={{ flexGrow: 1}}>{'>'}</ToggleButton>
    </ToggleButtonGroup>
  );
}