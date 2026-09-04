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
import AddIcon from '@mui/icons-material/Add';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import DevicesIcon from '@mui/icons-material/Devices';
import FileOpenIcon from '@mui/icons-material/FileOpen';
import InboxIcon from '@mui/icons-material/MoveToInbox';
import MailIcon from '@mui/icons-material/Mail';
import MenuIcon from '@mui/icons-material/Menu';
import SaveAsIcon from '@mui/icons-material/SaveAs';
import SaveIcon from '@mui/icons-material/Save';

//Table that holds Decision Table Rule Actions
export default function FileMenuList({editor}) {
  return (
    <List>
      {/* There should be a confirm dialog for the New DTP and New Config Buttons*/}

      {/*Table Options*/}
      <ListItem key="New Decision Table" disablePadding>
        <ListItemButton onClick={() => {editor.createNewTable()}}>
          <ListItemIcon>
            <AddIcon />
          </ListItemIcon>
          <ListItemText primary="New Decision Table" />
        </ListItemButton>
      </ListItem>

      <ListItem key="Open Decision Table" disablePadding>
        <ListItemButton onClick={() => {console.log("Will Open Decision Table")}}>
          <ListItemIcon>
            <FileOpenIcon />
          </ListItemIcon>
          <ListItemText primary="Open Decision Table" />
        </ListItemButton>
      </ListItem>

      <ListItem key="Save Decision Table" disablePadding>
        <ListItemButton onClick={() => {editor.outputDecisionTableXML()} /* Placeholder that sends it to the console instead of actually saving it*/}>
          <ListItemIcon>
            <SaveIcon />
          </ListItemIcon>
          <ListItemText primary="Save Decision Table" />
        </ListItemButton>
      </ListItem>

      <ListItem key="Save Decision Table As" disablePadding>
        <ListItemButton onClick={() => {editor.printDecisionTableXMLString()} /* Placeholder that sends it to the console instead of actually saving it*/}>
          <ListItemIcon>
            <SaveAsIcon />
          </ListItemIcon>
          <ListItemText primary="Save Decision Table As" />
        </ListItemButton>
      </ListItem>


      {/*Config Options*/}
      <ListItem key="New System Configuration" disablePadding>
        <ListItemButton onClick={() => {editor.createNewConfig()}}>
          <ListItemIcon>
            <AddIcon />
          </ListItemIcon>
          <ListItemText primary="New System Config" />
        </ListItemButton>
      </ListItem>

      <ListItem key="Open System Configuration" disablePadding>
        <ListItemButton onClick={() => {console.log("Will Open System Configuration")}}>
          <ListItemIcon>
            <FileOpenIcon />
          </ListItemIcon>
          <ListItemText primary="Open System Config" />
        </ListItemButton>
      </ListItem>

      <ListItem key="Save System Configuration" disablePadding>
        <ListItemButton onClick={() => {console.log("Will Save System Configuration")}}>
          <ListItemIcon>
            <SaveIcon />
          </ListItemIcon>
          <ListItemText primary="Save System Config" />
        </ListItemButton>
      </ListItem>

      <ListItem key="Save System Configuration As" disablePadding>
        <ListItemButton onClick={() => {console.log("Will Save System Configuration, giving them the option to rename")}}>
          <ListItemIcon>
            <SaveAsIcon />
          </ListItemIcon>
          <ListItemText primary="Save System Config As" />
        </ListItemButton>
      </ListItem>
    </List>
  )
}