import * as React from 'react';

// Material-UI Components
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import TextField from '@mui/material/TextField';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';

// Material-UI Icons
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';

//Custom Components
import RuleCondition from "@/components/RuleCondition";
import RuleComparison from "@/components/RuleComparison";

export default function RuleTable({ inputOrOutput }) {
    const [rows, setRows] = React.useState([]);
    const [row_ids, setRow_ids] = React.useState([]);

    const handleDeleteClick = (indexToRemove) => {
        const newRows = [...rows]; // Create a copy to avoid direct mutation
        newRows.splice(indexToRemove, 1);
        setRows(newRows);

        const newRowIds = [...row_ids]; // Create a copy to avoid direct mutation
        newRowIds.splice(indexToRemove, 1);
        setRow_ids(newRowIds);
    }

    function createData(id) {
        let name_box = <TextField id="device2" label="Device or Variable" variant="outlined" sx={{ flexGrow: 1, margin: 1 }}/>
        let comparison = <RuleComparison/>
        let value_box = <TextField id="value" label="value" variant="outlined" sx={{ flexGrow: 1, margin: 1 }}/>
        return {id, name_box, comparison, value_box};
    }

    const handleClick = () => {
        let currentrow = 1;
        if(row_ids.length > 0)
            currentrow=Math.max(...row_ids)+1;
        const newItem = createData(currentrow);
        setRows([...rows, newItem]);
        setRow_ids([...row_ids, currentrow]);
    }

    return (
        <Box sx={{ flexGrow: 1, margin: 1 }}>
            <Typography variant="p" component="div" sx={{ flexGrow: 1 }}>
                { inputOrOutput } Conditions
                <IconButton onClick={handleClick} aria-label="add new condition">
                    <AddIcon />
                </IconButton>
            </Typography>
            <TableContainer component={Paper}>
                <Table sx={{ minWidth: 650 }} aria-label="simple table">
                    <TableHead style={{backgroundColor:"primary"}}>
                        <TableRow color="primary">
                            <TableCell>Rule Number</TableCell>
                            <TableCell align="right">Device or Variable</TableCell>
                            <TableCell align="right">Comparison</TableCell>
                            <TableCell align="right">Value</TableCell>
                            <TableCell align="right"></TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {rows.map((row, index) => (
                            <TableRow
                                key={row.id}
                                sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                            >
                                <TableCell component="th" scope="row">
                                    {row.id}
                                </TableCell>
                                <TableCell align="right">{row.name_box}</TableCell>
                                <TableCell align="right">{row.comparison}</TableCell>
                                <TableCell align="right">{row.value_box}</TableCell>
                                <TableCell align="right">{<IconButton onClick={() => handleDeleteClick(index)} aria-label="add new condition"><DeleteIcon/></IconButton>}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
}