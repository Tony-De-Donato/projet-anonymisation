import {createHashRouter, createRoutesFromElements, Route, RouterProvider} from 'react-router-dom';
import SearchRulesView from './views/SearchRulesView';
import FileUploadView from './views/FileUploadView';
import store, {persistor} from "./redux/store";

import {Provider} from 'react-redux';
import {PersistGate} from "redux-persist/integration/react";

const App = () => {
    const router = createHashRouter(
        createRoutesFromElements(
            <Route>
                <Route path="/" element={<SearchRulesView/>}/>
                <Route path="/upload" element={<FileUploadView/>}/>
            </Route>
        )
    );

    return (
        <Provider store={store}>
            <PersistGate persistor={persistor} loading={null}>
                <RouterProvider router={router}/>
            </PersistGate>
        </Provider>
    );
};

export default App;
