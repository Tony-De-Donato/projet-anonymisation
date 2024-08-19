import React from 'react';
import {Alert, Grid, Paper,} from '@mui/material';
import CircularProgress from "@mui/material/CircularProgress";

interface FileLoadingAndErrorProps {
    isLoading: boolean;
    error: string | null;
}

const FileLoadingAndError: React.FC<FileLoadingAndErrorProps> = ({
                                                                     isLoading,
                                                                     error
                                                                 }) => {


    return (

        <Grid container
              style={{
                  zIndex: 100,
                  position: 'fixed',
                  top: 0,
                  right: 0,
              }}
              justifyContent="flex-end"
              alignItems="flex-start"
        >
            <Grid item>
                <Paper style={{textAlign: 'center', fontSize: "x-large"}}>
                    {isLoading && <p style={{padding: "10px 20px 10px 20px"}}><CircularProgress/></p>}
                    {error && <p><Alert severity="error">{error}</Alert></p>}
                </Paper>
            </Grid>
        </Grid>

    );
};

export default FileLoadingAndError;

