import React from 'react';
import {Grid, Typography} from '@mui/material';
import {Link} from 'react-router-dom';
import Button from '@mui/material/Button';

const Header: React.FC = () => (
    <Grid item xs={12} borderBottom={1} padding={2} bgcolor={'#2d3c5e'} color={'white'} display={'flex'}
          justifyContent={'space-between'}>
        <Typography variant="h4">Anonymization App</Typography>
        <Grid item xs={4} display={'flex'} justifyContent={'space-between'}>
            <Button component={Link} to="/" color="inherit">Global view</Button>
            <Button component={Link} to="/rules" color="inherit">Search rules</Button>
            <Button component={Link} to="/upload" color="inherit">File upload</Button>
        </Grid>
    </Grid>
);

export default Header;
