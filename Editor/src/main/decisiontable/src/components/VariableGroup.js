import * as React from 'react';

// Material-UI Components
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import Paper from '@mui/material/Paper';
import TextField from '@mui/material/TextField';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';

// Material-UI Icons
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';

//Table that holds all variables in a variable group
export default function VariableGroup({variableGroup,editor,varGroupIndex,handleSave}) {
  //Loads rows from editor's variable group
  function getExistingRows() {
    let startRows = [];
    let startRowIDs = [];
    for(let variable = 0; variable < variableGroup.variables.length; variable++) {
      const newItem = createData(variable,variableGroup.variables[variable].name,variableGroup.variables[variable].value,variableGroup.variables[variable].type);
      startRows.push(newItem);
      startRowIDs.push(variable);
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
  function createData(id,name,value,type) {
    return {id, name, value, type};
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

  //Function to handle text input and update the rows accordingly
  //This handles the updates for all of the text boxes, in all rows and handling name, type, and value
  const handleChange = (event,index,field) => {
    const newRows = [...rows]; // Create a copy to avoid direct mutation

    switch(field) {
      case "name":
        newRows[index].name = event.target.value;
        break;
      case "type":
        newRows[index].type = event.target.value;
        break;
      case "value":
        newRows[index].value = event.target.value;
        break;
    }

    setRows(newRows);
  }

  return (
    <Box sx={{ flexGrow: 1, margin: 1 }}>
      <Typography variant="p" component="div" sx={{ flexGrow: 1 }}>
        Variables
        <IconButton onClick={handleAddButtonClick} aria-label="add new condition">
          <AddIcon />
        </IconButton>
        <Button variant="contained" onClick={() => handleSave([...rows])}>Save</Button>
      </Typography>
      <TableContainer component={Paper}>
        <Table sx={{ minWidth: 650 }} aria-label="simple table">
          <TableHead style={{backgroundColor:"primary"}}>
            <TableRow color="primary">
              <TableCell align="left">Name</TableCell>
              <TableCell align="right">Data Type</TableCell>
              <TableCell align="right">Starting Value</TableCell>
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
                    id="name"
                    label="Name"
                    variant="outlined"
                    defaultValue={row.name}
                    onChange={(e)=>handleChange(e,index,"name")}
                    sx={{ flexGrow: 1, margin: 1 }}
                  />
                </TableCell>
                {/*Should possibly be switched to dropdown*/}
                <TableCell align="right">
                  <TextField
                    id="type"
                    label="Data Type"
                    variant="outlined"
                    defaultValue={row.type}
                    onChange={(e)=>handleChange(e,index,"type")}
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
                <TableCell align="right">
                  <IconButton onClick={() => handleDeleteClick(index)} aria-label="Delete Row"><DeleteIcon/></IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}