import React, {Component} from 'react';
import {Alert, Button, Col, Form, Row} from 'react-bootstrap';
import axios from 'axios';

export default class Register extends Component {
    constructor(props) {
        super(props);
        this.state = this.getDefaultState();
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    getDefaultState = () => {
        return {
            pingUrl: '',
            quota: {
                instances: 1,
                memory: '',
                disk: ''
            },
            credentials: {
                user: '',
                password: ''
            },
            showAlert: false,
            error: {
                statusCode: '',
                message: '',
                timestamp: '',
            }
        }
    };

    toBytes = data => {
        var bytes = [];
        for (var i = 0; i < data.length; ++i) {
            bytes.push(data.charCodeAt(i) & 0xFF);
        }
        return bytes;
    };

    handleSubmit = async (event) => {
        event.preventDefault();
        // event.stopPropagation();
        // const state = this.state;

        let data = {
            pingUrl: event.target.pingUrl.value,
            quota: {
                instances: parseInt(event.target.instances.value),
                memory: event.target.memory.value,
                disk: event.target.disk.value
            },
            credentials: {
                user: event.target.user.value,
                password: this.toBytes(event.target.password.value)
            }
        };

        event.target.pingUrl.value = '';
        event.target.instances.value = '';
        event.target.memory.value = '';
        event.target.disk.value = '';
        event.target.user.value = '';
        event.target.password.value = '';

        await axios.post(`https://cloud-availability-service.cfapps.eu10.hana.ondemand.com/service/api/v1/encrypt`, data.credentials.password)
            .then(res => {
                data.credentials.password = res.data;
            });

        let config = {
            headers: {
                'Content-Type': 'application/json',
            }
        };

        await axios.post(`https://cloud-availability-service.cfapps.eu10.hana.ondemand.com/service/api/v1/callbacksUi`, data, config)
            .then(res => {
                this.setState({ showAlert: true, pingUrl: '', quota: {}, credentials: {} })
            })
            .catch(error => {
                this.setState({
                    showAlert: true,
                    error: {
                        statusCode: error.response.status + ' - ' + error.response.statusText,
                        message: error.response.data,
                        timestamp: new Date().toLocaleString()
                    }
                });

            })
    };

    handleChange = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };

    handlePasswordChange = (event) => {
        let toUpdate = this.state.credentials;
        let name = event.target.name;
        let value = this.toBytes(event.target.value);

        toUpdate[name] = value;
        this.setState({toUpdate});
    };

    handleInstancesChange = (event) => {
        let toUpdate = this.state.credentials;
        let name = event.target.name;
        let value = parseInt(event.target.value);

        toUpdate[name] = value;
        this.setState({toUpdate});
    };

    handleQuotaChange = (event) => {
        this.updateState(event, this.state.quota);
    };

    handleCredentialsChange = (event) => {
        this.updateState(event, this.state.credentials);
    };

    updateState = (event, toUpdate) => {
        let name = event.target.name;
        let value = event.target.value;

        toUpdate[name] = value;
        this.setState({toUpdate});
    };

    showErrorAlert = () => {
        return (
            <Alert className="mt-5" variant="danger" onClose={() => this.setState({showAlert: false, error: {}})} dismissible>
                <Alert.Heading>Received status <strong>{this.state.error.statusCode}</strong></Alert.Heading>
                <p className="m-0">{this.state.error.message}</p>
                <hr/>
                <p className="m-0">{this.state.error.timestamp}</p>
            </Alert>
        );
    };

    showSuccessAlert = () => {
        return (
            <Alert className="mt-5" variant="success" onClose={() => this.setState({showAlert: false})} dismissible>
                <p className="m-0">Successfully registered a callback!</p>
            </Alert>
        );
    };

    render() {
        return (
            <div className="container jumbotron transparent">
                <h1 className="display-4 text-secondary pb-5">Register Callback</h1>
                <Form size="lg" onSubmit={this.handleSubmit}>

                    <Form.Group className="align-items-center align-middle" as={Row} controlId="formPingUrl">
                        <Form.Label className="text-info" column sm="2">Ping URL</Form.Label>
                        <Col sm="10">
                            <Form.Control name="pingUrl" type="text" placeholder="https://" aria-describedby="formPingUrl" required/>
                        </Col>
                    </Form.Group>

                    <hr className="border-secondary mt-5 mb-4"/>
                    <h5 className="text-secondary pb-4">Quota</h5>

                    <Form.Group className="align-items-center align-middle" as={Row} controlId="formQuotaInstances">
                        <Form.Label className="text-info" column sm="2">Instances</Form.Label>
                        <Col sm="10">
                            <Form.Control name="instances" as="select" aria-describedby="formQuotaInstances" required>
                                <option>1</option>
                                <option>2</option>
                                <option>3</option>
                                <option>4</option>
                                <option>5</option>
                            </Form.Control>
                        </Col>
                    </Form.Group>

                    <Form.Group className="align-items-center align-middle" as={Row} controlId="formQuotaMemory">
                        <Form.Label className="text-info" column sm="2">Memory</Form.Label>
                        <Col sm="10">
                            <Form.Control name="memory" type="text" placeholder="1024M, 1G, etc." aria-describedby="formQuotaMemory" required/>
                        </Col>
                    </Form.Group>

                    <Form.Group className="align-items-center align-middle" as={Row} controlId="formQuotaDisk">
                        <Form.Label className="text-info" column sm="2">Disk</Form.Label>
                        <Col sm="10">
                            <Form.Control name="disk" type="text" placeholder="1024M, 1G, etc." aria-describedby="formQuotaDisk" required/>
                        </Col>
                    </Form.Group>

                    <hr className="border-secondary mt-5 mb-4"/>
                    <h5 className="text-secondary pb-4">Credentials</h5>

                    <Form.Group className="align-items-center align-middle" as={Row} controlId="formUser">
                        <Form.Label className="text-info" column sm="2">User</Form.Label>
                        <Col sm="10">
                            <Form.Control name="user" type="text" placeholder="johndoe@mail.com" aria-describedby="formUser" required/>
                        </Col>
                    </Form.Group>

                    <Form.Group className="align-items-center align-middle" as={Row} controlId="formPassword">
                        <Form.Label className="text-info" column sm="2">Password</Form.Label>
                        <Col sm="10">
                            <Form.Control name="password" type="password" placeholder="Password" aria-describedby="formPassword" required/>
                        </Col>
                    </Form.Group>

                    <Button className="mt-5" variant="success" size="lg" type="submit">Register</Button>
                </Form>

                {this.state.showAlert ? (this.state.error.statusCode ? this.showErrorAlert() : this.showSuccessAlert()) : <div/>}

            </div>
        );
    }
}