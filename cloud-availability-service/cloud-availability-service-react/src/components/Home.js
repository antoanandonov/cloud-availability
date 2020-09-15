import React, {Component} from 'react';
import {Link} from "react-router-dom";
import logo from './../react-spring@4x_gradient.png';

export default class Home extends Component {

    render() {
        return (
            <div className="jumbotron jumbotron-fluid transparent">
                <img src={logo} className="App-logo mb-5" alt="logo"/>
                <div className="container">
                    <h1 className="display-4 text-secondary">Cloud Availability</h1>
                    <p className="lead primaryColor text-justify">Availability is the ability of a system to provide the expected functionality to its users.</p>
                    <hr className="my-4 mt-5 mb-5"/>
                    <p className="lead">
                        <Link className="btn btn-lg btn-outline-warning" to="/register">Register URL</Link>
                    </p>
                </div>
            </div>
        );
    }
}