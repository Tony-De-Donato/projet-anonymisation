import React from 'react';
import {Box, Grid} from '@mui/material';
import {Link} from 'react-router-dom';
import Button from '@mui/material/Button';

const Header: React.FC = () => (
    <Grid item xs={12} borderBottom={1} padding={2} bgcolor={'#2d3c5e'} color={'white'} display={'flex'}
          justifyContent={'space-between'}>
        <Box component={"img"} src={'/https://github.githubassets.com/assets/gh-desktop-7c9388a38509.png'} alt={'logo'}
             height={60}
             style={{backgroundColor: 'rgba(255,255,255,0.63)', borderRadius: 10, padding: 5}}/>
        {/*<Typography variant="h4">Anonymization App</Typography>*/}
        <Grid item xs={2} display={'flex'} justifyContent={'space-between'}>
            <Button component={Link} to="/" color="inherit">Search rules</Button>
            <Button component={Link} to="/upload" color="inherit">File upload</Button>
        </Grid>
    </Grid>
);

export default Header;
