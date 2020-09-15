import React, {Component} from 'react';
import {BrowserRouter as Router, Link, Route, Switch} from "react-router-dom";
import logo from './react-spring@4x_gradient.png';
import './App.css';
import Callbacks from './components/Callbacks';
import Register from './components/Register';
import Home from './components/Home';
import Error from './components/Error'
import axios from 'axios';

export default class App extends Component {
    state = {
        callbacks: [],
        showAlert: false,
        error: {}
    };

    componentDidMount() {
        this.fetchCallbacks();
        this.interval = setInterval(() => this.fetchCallbacks(), 3000);
    }

    componentWillUnmount() {
        clearInterval(this.interval);
    }

    fetchCallbacks = async () => {

        await axios.get(`https://cloud-availability-service.cfapps.eu10.hana.ondemand.com/service/api/v1/callbacks`)
        .then(res => {
            this.setState({callbacks: res.data, showAlert: false});
        })
        .catch(err => {
            console.error(err);
            if(err.message === 'Network Error'){
                this.setState({ 
                    showAlert: true,
                    error: {
                        message: 'The information on the page could not be synced with the server.'
                    }
                });
            }
        })
        
    };

    render() {
        return (
            <div className="App container-fluid p-0">
                <Router>
                    <header>
                        <nav className="navbar navbar-expand-sm navbar-dark border-bottom border-dark">
                            <img src={logo} className="App-logo-header d-inline-block align-top" width="50" height="50" alt="React-Bootstrap"/>
                            <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Show menu">
                                <span className="navbar-toggler-icon"></span>
                            </button>
                            <div className="navbar-collapse collapse" id="navbarSupportedContent">
                                <div className="mx-auto">
                                    <ul className="navbar-nav mr-auto">
                                        <li className="nav-item">
                                            <Link className="nav-link" to="/">Home</Link>
                                        </li>
                                        <li className="nav-item">
                                            <Link className="nav-link" to="/health">Health</Link>
                                        </li>
                                    </ul>
                                </div>
                                <div>
                                    <ul className="navbar-nav ml-auto">
                                        <li className="nav-item ">
                                            <Link className="btn btn-sm btn-outline-secondary" to="/register">Register</Link>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </nav>
                    </header>

                    <div className="App-body">
                        <Switch>
                            <Route exact path="/">
                                <Home/>
                            </Route>
                            <Route path="/register">
                                <Register/>
                            </Route>
                            <Route path="/health">
                                <Callbacks {...this.state} />
                            </Route>
                            <Route path="*" component={Error} status={404}/>
                        </Switch>
                    </div>
                </Router>

            </div>
        );
    }

}
