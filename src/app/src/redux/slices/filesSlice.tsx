import {createSlice, PayloadAction} from '@reduxjs/toolkit';
import {AnonymizedResponse} from "../../interfaces/AnonymizedResponse";

interface FilesState {
    loading: boolean;
    anonymizedData: AnonymizedResponse | null;
    error: string | null;

}

const initialState: FilesState = {
    loading: false,
    anonymizedData: null,
    error: null,
};


const filesSlice = createSlice({
    name: 'files',
    initialState,
    reducers: {
        setLoading: (state, action: PayloadAction<boolean>) => {
            state.loading = action.payload;
        },
        setAnonymizedData: (state, action: PayloadAction<AnonymizedResponse | null>) => {
            state.anonymizedData = action.payload;
        },
        setError: (state, action: PayloadAction<string | null>) => {
            state.error = action.payload;
        }
    },
});


export const {setLoading, setAnonymizedData, setError} = filesSlice.actions;
export default filesSlice.reducer;

