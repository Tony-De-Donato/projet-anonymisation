import {createSlice, PayloadAction} from '@reduxjs/toolkit';
import {RegexRule} from '../../interfaces/RegexRule';

interface RulesState {
    rules: RegexRule[];
    selectedRules: RegexRule[];
}

const initialState: RulesState = {
    rules: [],
    selectedRules: [],
};

const rulesSlice = createSlice({
    name: 'rules',
    initialState,
    reducers: {
        setRules: (state, action: PayloadAction<RegexRule[]>) => {
            state.rules = action.payload;
        },
        setSelectedRules: (state, action: PayloadAction<RegexRule[]>) => {
            state.selectedRules = action.payload;
        }

    },
});

export const {setRules, setSelectedRules} = rulesSlice.actions;
export default rulesSlice.reducer;

