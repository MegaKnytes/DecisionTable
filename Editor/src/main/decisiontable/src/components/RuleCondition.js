'use client'

import * as React from 'react'
import {AppBar, Box, Button, IconButton, Toolbar, Typography, TextField, ToggleButtonGroup, ToggleButton} from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';


export default function RuleCondition() {
    const [alignment, setAlignment] = React.useState('web');

    const handleChange = (event, newAlignment) => {
        setAlignment(newAlignment);
    };

    return (
        <Box sx={{ flexGrow: 1, margin: 1 }} display="flex">
            <TextField id="device" label="Device or Variable" variant="outlined" sx={{ flexGrow: 1, margin: 1 }}/>
            <ToggleButtonGroup color="primary" value={alignment} exclusive onChange={handleChange} aria-label="Platform" sx={{ flexGrow: 1}}>
                <ToggleButton value="eql" sx={{ flexGrow: 1}}>=</ToggleButton>
                <ToggleButton value="neq" sx={{ flexGrow: 1}}>!=</ToggleButton>
                <ToggleButton value="ltn" sx={{ flexGrow: 1}}>{'<'}</ToggleButton>
                <ToggleButton value="gtr" sx={{ flexGrow: 1}}>{'>'}</ToggleButton>
            </ToggleButtonGroup>
            <TextField id="value" label="value" variant="outlined" sx={{ flexGrow: 1, margin: 1 }}/>
            <IconButton aria-label="add new condition">
                <DeleteIcon />
            </IconButton>
        </Box>
    );
}