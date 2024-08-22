// index.tsx
import React from 'react';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import 'bootstrap/dist/css/bootstrap.min.css';
import {Provider} from 'react-redux';
import store, {persistor} from "./redux/store";
import ReactDOM from 'react-dom';
import {PersistGate} from "redux-persist/integration/react";
import {Spinner} from "reactstrap";

ReactDOM.render(
    <React.StrictMode>
        <Provider store={store}>
            <PersistGate persistor={persistor} loading={<Spinner/>}>
                <App/>
            </PersistGate>
        </Provider>
    </React.StrictMode>,
    document.getElementById('root')
);

reportWebVitals();
