import React from 'react';
import { Link } from "react-router-dom";

const Error = ({ location }) => (
    <div className="jumbotron jumbotron-fluid transparent">
        <div className="container">
            <h1 className="text-danger my-4 display-1 font-weight-bold">{404}</h1>
            <h3 className="text-danger">The page with sub-location <code className="text-danger  font-weight-bold">{location.pathname}</code> wasn't found!</h3>
            <hr className="my-4 mt-5 mb-5"/>
            <p className="lead">
                <Link className="btn btn-lg btn-outline-primary" to="/">Go Back!</Link>
            </p>
        </div>
    </div>
);

export default Error;