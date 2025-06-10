'use client'

import * as React from 'react'
import {AppBar, Box, Button, IconButton, Toolbar, Typography, TextField, ToggleButtonGroup, ToggleButton} from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';


export default function RuleComparison() {
    const [alignment, setAlignment] = React.useState('web');

    const handleChange = (event, newAlignment) => {
        setAlignment(newAlignment);
    };

    return (
        <ToggleButtonGroup color="primary" value={alignment} exclusive onChange={handleChange} aria-label="Platform" sx={{ flexGrow: 1}}>
            <ToggleButton value="eql" sx={{ flexGrow: 1}}>=</ToggleButton>
            <ToggleButton value="neq" sx={{ flexGrow: 1}}>!=</ToggleButton>
            <ToggleButton value="ltn" sx={{ flexGrow: 1}}>{'<'}</ToggleButton>
            <ToggleButton value="gtr" sx={{ flexGrow: 1}}>{'>'}</ToggleButton>
        </ToggleButtonGroup>
    );
}