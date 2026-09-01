import * as React from 'react';

// Material-UI Components
import Container from '@mui/material/Container';
import Divider from '@mui/material/Divider';
import Drawer from '@mui/material/Drawer';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import {Box, Button, IconButton, Toolbar, Typography, ButtonGroup} from "@mui/material";

// Material-UI Icons
import DevicesIcon from '@mui/icons-material/Devices';
import FormatListNumberedIcon from '@mui/icons-material/FormatListNumbered';

//Table that holds Decision Table Rule Actions
export default function ViewMenuList({handleVariableClick,handleDeviceClick,handleRuleClick,handleStatesClick}) {
  return (
    <List>
      <ListItem key="Variables" disablePadding>
        <ListItemButton onClick={handleVariableClick}>
          <ListItemIcon>
            <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#0000008A"><path d="M120-280v-400h720v400H120Zm80-80h560v-240H200v240Zm0 0v-240 240Z"/></svg>
          </ListItemIcon>
          <ListItemText primary="Variables" />
        </ListItemButton>
      </ListItem>
      <ListItem key="Devices" disablePadding>
        <ListItemButton onClick={handleDeviceClick}>
          <ListItemIcon>
            <DevicesIcon />
          </ListItemIcon>
          <ListItemText primary="Devices" />
        </ListItemButton>
      </ListItem>
      <ListItem key="Rules" disablePadding>
        <ListItemButton onClick={handleRuleClick}>
          <ListItemIcon>
            <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#0000008A"><path d="m576-160-56-56 104-104-104-104 56-56 104 104 104-104 56 56-104 104 104 104-56 56-104-104-104 104Zm79-360L513-662l56-56 85 85 170-170 56 57-225 226ZM80-280v-80h360v80H80Zm0-320v-80h360v80H80Z"/></svg>
          </ListItemIcon>
          <ListItemText primary="Rules" />
        </ListItemButton>
      </ListItem>
      <ListItem key="State Machines" disablePadding>
        <ListItemButton onClick={handleStatesClick}>
          <ListItemIcon>
            <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#0000008A"><path d="M296-270q-42 35-87.5 32T129-269q-34-28-46.5-73.5T99-436l75-124q-25-22-39.5-53T120-680q0-66 47-113t113-47q66 0 113 47t47 113q0 66-47 113t-113 47q-9 0-18-1t-17-3l-77 130q-11 18-7 35.5t17 28.5q13 11 31 12.5t35-12.5l420-361q42-35 88-31.5t80 31.5q34 28 46 73.5T861-524l-75 124q25 22 39.5 53t14.5 67q0 66-47 113t-113 47q-66 0-113-47t-47-113q0-66 47-113t113-47q9 0 17.5 1t16.5 3l78-130q11-18 7-35.5T782-630q-13-11-31-12.5T716-630L296-270Zm40.5-353.5Q360-647 360-680t-23.5-56.5Q313-760 280-760t-56.5 23.5Q200-713 200-680t23.5 56.5Q247-600 280-600t56.5-23.5Zm400 400Q760-247 760-280t-23.5-56.5Q713-360 680-360t-56.5 23.5Q600-313 600-280t23.5 56.5Q647-200 680-200t56.5-23.5ZM280-680Zm400 400Z"/></svg>
          </ListItemIcon>
          <ListItemText primary="State Machines" />
        </ListItemButton>
      </ListItem>
    </List>
  )
}