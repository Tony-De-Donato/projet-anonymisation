import {configureStore} from '@reduxjs/toolkit';
import {persistReducer, persistStore} from 'redux-persist';
import storage from 'redux-persist/lib/storage';
import rulesReducer from './slices/rulesSlice';
import filesReducer from './slices/filesSlice';

const persistConfig = {
    key: 'root',
    storage,
    whitelist: ['rules', 'selectedRules', 'loading', 'anonymizedData', 'error']
};

const persistedRulesReducer = persistReducer(persistConfig, rulesReducer);
const persistedFilesReducer = persistReducer(persistConfig, filesReducer);


const store = configureStore({
    reducer: {
        rules: persistedRulesReducer,
        files: persistedFilesReducer,

    },

});
export type RootState = ReturnType<typeof store.getState>;
export const persistor = persistStore(store);
export default store;
