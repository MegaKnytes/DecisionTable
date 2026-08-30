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
import RuleComparison from "@/components/RuleComparison";

//Table that holds Decision Table Rule Actions
export default function ActionTable({rule,editor,ruleIndex,handleActions}) {
  //Loads rows from editor's variable group
  function getExistingRows() {
    let startRows = [];
    let startRowIDs = [];
    for(let action = 0; action < rule.actions.length; action++) {
      console.log(rule.actions[action]);
      const newItem = createData(action,rule.actions[action].target,rule.actions[action].value);
      startRows.push(newItem);
      startRowIDs.push(action);
    }
    return([startRows,startRowIDs]);
  }
  const [startRows,startRowIDs] = getExistingRows();

  //Constants to track all of the rows that store variables
  const [rows, setRows] = React.useState(startRows);
  const [row_ids, setRow_ids] = React.useState(startRowIDs);

  //Function to add new rows on button press
  const handleAddButtonClick = () => {
    let currentrow = 1;
    if(row_ids.length > 0)
      currentrow=Math.max(...row_ids)+1;
    const newItem = createData(currentrow,"","","");
    setRows([...rows, newItem]);
    setRow_ids([...row_ids, currentrow]);
  }

  //Function to create the data that is stored in a row. This might be able to be done inline
  function createData(id,target,value) {
    return {id,target,value};
  }

  //Function to remove rows on delete
  const handleDeleteClick = (indexToRemove) => {
    const newRows = [...rows]; // Create a copy to avoid direct mutation
    newRows.splice(indexToRemove, 1);
    setRows(newRows);

    const newRowIds = [...row_ids]; // Create a copy to avoid direct mutation
    newRowIds.splice(indexToRemove, 1);
    setRow_ids(newRowIds);
  }

  const handleChange = (event,index,field) => {
    const newRows = [...rows]; // Create a copy to avoid direct mutation

    switch(field) {
      case "target":
        newRows[index].target = event.target.value;
        break;
      case "type":
        newRows[index].type = event.target.value;
        break;
      case "value":
        newRows[index].value = event.target.value;
        break;
    }

    setRows(newRows);

    handleActions([...rows]);
  }

  return (
    <Box sx={{ flexGrow: 1, margin: 1 }}>
      <Typography variant="p" component="div" sx={{ flexGrow: 1 }}>
        Actions
        <IconButton onClick={handleAddButtonClick} aria-label="add new Action">
          <AddIcon />
        </IconButton>
      </Typography>
      <TableContainer component={Paper}>
        <Table sx={{ minWidth: 650 }} aria-label="simple table">
          <TableHead style={{backgroundColor:"primary"}}>
            <TableRow color="primary">
              <TableCell align="left">Target</TableCell>
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
                <TableCell align="left">
                  <TextField
                    id="target"
                    label="Target"
                    variant="outlined"
                    defaultValue={row.target}
                    onChange={(e)=>handleChange(e,index,"target")}
                    sx={{ flexGrow: 1, margin: 1 }}
                  />
                </TableCell>
                <TableCell align="right">
                  <TextField
                    id="value"
                    label="Value"
                    variant="outlined"
                    defaultValue={row.value}
                    onChange={(e)=>handleChange(e,index,"value")}
                    sx={{ flexGrow: 1, margin: 1 }}
                  />
                </TableCell>
                <TableCell align="right">{<IconButton onClick={() => handleDeleteClick(index)} aria-label="add new Action"><DeleteIcon/></IconButton>}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}