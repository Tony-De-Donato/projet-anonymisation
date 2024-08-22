import React from 'react';
import {Alert, Grid, Paper,} from '@mui/material';
import CircularProgress from "@mui/material/CircularProgress";
import {useDispatch, useSelector} from "react-redux";
import Button from "@mui/material/Button";
import {setError} from "../redux/slices/filesSlice";


interface FileLoadingAndErrorProps {
}

const FileLoadingAndError: React.FC<FileLoadingAndErrorProps> = () => {

    const isLoading = useSelector((state: any) => state.files.loading);
    const error = useSelector((state: any) => state.files.error);

    const dispatch = useDispatch();
    const handleClose = () => {
        dispatch(setError(null));
    }

    return (

        <Grid container
              style={{
                  zIndex: 100,
                  position: 'fixed',
                  top: 10,
                  right: 10,
              }}
              justifyContent="flex-end"
              alignItems="flex-start"
        >
            <Grid item>
                <Paper style={{
                    textAlign: 'center',
                    fontSize: "x-large",
                    backgroundColor: "rgb(237,237,237)"
                }}>
                    {isLoading && <p style={{padding: "10px 20px 10px 20px"}}><CircularProgress/></p>}
                    {error && <p>
                        <Alert severity="error">{error}
                            <Button onClick={handleClose} color={"error"} style={{margin: "0", padding: "0"}}>X</Button>
                        </Alert>
                    </p>}
                </Paper>
            </Grid>
        </Grid>

    );
};

export default FileLoadingAndError;

