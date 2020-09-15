import React, {Component} from 'react';
import Callback from './Callback';
import {Alert} from 'react-bootstrap';

export default class Callbacks extends Component {

    showAlert = () => {
        const {error} = this.props;
        return (
            <Alert className="mb-5"  variant="danger" >
                <Alert.Heading>A network error occurred!</Alert.Heading>
                <p className="m-0">{error.message}</p>
            </Alert>
        );
    }

    render() {
        const {callbacks, showAlert} = this.props;
        return (
            <div className="App container-fluid margin-top-bottom">
                { showAlert ? this.showAlert() :  <div/> }
                <h1 className="display-4 text-secondary pb-5">Health Status</h1>
                <table className="table table-responsive-sm table-borderless table-hover shadow-sm">
                    <thead>
                    <tr className='primaryColor'>
                        <th scope="col">#</th>
                        <th scope="col">Callback ID</th>
                        <th scope="col">State</th>
                    </tr>
                    </thead>
                    <tbody className="border-top border-dark">
                    {
                        callbacks.filter(Boolean).map((callback, i) => {
                            return (<Callback key={callback.callbackId} {...callback} index={i + 1}/>);
                        })
                    }
                    </tbody>
                </table>
            </div>
        );
    }

}