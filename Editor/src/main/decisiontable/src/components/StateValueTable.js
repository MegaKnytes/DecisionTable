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

//Custom Components

//Table that holds all possible states for a state machine
export default function StateValueTable({state,editor,stateIndex,handleSave}) {
  //Loads rows from editor's variable group
  function getExistingRows() {
    let startRows = [];
    let startRowIDs = [];
    for(let value = 0; value < state.values.length; value++) {
      const newItem = createData(value, state.values[value].name);
      startRows.push(newItem);
      startRowIDs.push(value);
    }
    return([startRows,startRowIDs]);
  }
  const [startRows,startRowIDs] = getExistingRows();

  //Constants to track all of the rows that store values
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
  const handleChange = (event,index) => {
    const newRows = [...rows]; // Create a copy to avoid direct mutation
    newRows[index].name = event.target.value;
    setRows(newRows);
  }

  return (
    <Box sx={{ flexGrow: 1, margin: 1 }}>
      <Typography variant="p" component="div" sx={{ flexGrow: 1 }}>
        States
        <IconButton onClick={handleAddButtonClick} aria-label="add new condition">
          <AddIcon />
        </IconButton>
        <Button variant="contained" onClick={() => handleSave([...rows])}>Save</Button>
      </Typography>
      <TableContainer component={Paper}>
        <Table sx={{ minWidth: 650 }} aria-label="simple table">
          <TableHead style={{backgroundColor:"primary"}}>
            <TableRow color="primary">
              <TableCell align="left">State Name</TableCell>
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
                    onChange={(e)=>handleChange(e,index)}
                    sx={{ flexGrow: 1, margin: 1 }}
                  />
                </TableCell>
                <TableCell align="right">{<IconButton onClick={() => handleDeleteClick(index)} aria-label="Remove condition"><DeleteIcon/></IconButton>}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}