import React, {Component} from 'react';
import {Accordion, Button, Card} from 'react-bootstrap';
import {CopyToClipboard} from 'react-copy-to-clipboard';
import {IoIosCopy} from 'react-icons/io';
import {MdDelete} from 'react-icons/md';
import axios from 'axios';

export default class Callback extends Component {

    formatState = (state) => {
        switch (state) {
            case 'OK':
                return 'badge-success';
            case 'WARNING':
                return 'badge-warning';
            case 'CRITICAL':
                return 'badge-danger';
            case 'NOT_FOUND':
                return 'badge-secondary';
            case 'NOT_AVAILABLE':
                return 'badge-info';
            case 'UNSTABLE':
                return 'badge-warning';
            case 'UNKNOWN':
                return 'badge-dark';
            default:
                return 'badge-secondary';
        }
    };

    formatStateText = (state) => {
        switch (state) {
            case 'OK':
                return 'Ok';
            case 'WARNING':
                return 'Warning';
            case 'CRITICAL':
                return 'Critical';
            case 'NOT_FOUND':
                return 'Not Found';
            case 'NOT_AVAILABLE':
                return 'Not Available';
            case 'UNSTABLE':
                return 'Unstable';
            case 'UNKNOWN':
                return 'Unknown';
            default:
                return 'Not Available';
        }
    };

    handleSubmit = async (event) => {
        event.preventDefault();
        const {callbackId} = this.props;

        await axios.delete(`https://cloud-availability-service.cfapps.eu10.hana.ondemand.com/service/api/v1/callbacks/` + callbackId)
            .catch(error => {
                console.log(error);
            });
    }

    render() {
        const {index, callbackId, pingUrl, quota, healthStatus} = this.props;
        return (
            <tr className="border-top border-bottom border-dark">
                <td className="text-secondary align-middle text-center">
                    <span className='primaryColor'>{index}</span>
                </td>
                <td>
                    <Accordion className='container align-self-stretch align-content-stretch'>
                        <Card className='transparent no-border'>
                            <Card.Header className='transparent align-items-center'>
                                <Accordion.Toggle as={Button} variant="text" eventKey={index}>
                                    <div className='text-justify align-self-center align-middle text-white'> {callbackId}</div>
                                </Accordion.Toggle>
                                <CopyToClipboard text={callbackId}>
                                    <Button id='coppyToClipboard' size="lg" variant="link"><IoIosCopy/></Button>
                                </CopyToClipboard>
                                <Button variant="link" size="lg" type="submit" onClick={this.handleSubmit}><MdDelete id='unscheduleCallback' className='text-danger'/></Button>
                            </Card.Header>
                            <Accordion.Collapse eventKey={index}>
                                <Card.Body className='align-items-center align-middle'>
                                    <Card.Text className="d-flex justify-content-around inline-block">
                                        <label className="d-inline text-info">Instances: {quota.instances}</label>
                                        <label className="d-inline text-info">|</label>
                                        <label className="d-inline text-info">Memory: {quota.memory}</label>
                                        <label className="d-inline text-info">|</label>
                                        <label className="d-inline text-info">Disk: {quota.disk}</label>
                                    </Card.Text>
                                    <Card.Footer className="transparent pt-4 text-muted">
                                        <a href={pingUrl} target='_blank' rel="noopener noreferrer">{pingUrl}</a>
                                    </Card.Footer>
                                </Card.Body>
                            </Accordion.Collapse>
                        </Card>
                    </Accordion>
                </td>
                <td className="align-middle text-justify text-center">
                    <span className={"badge-lg badge-pill " + this.formatState(healthStatus)}>{this.formatStateText(healthStatus)}</span>
                </td>
            </tr>
        );
    }

}