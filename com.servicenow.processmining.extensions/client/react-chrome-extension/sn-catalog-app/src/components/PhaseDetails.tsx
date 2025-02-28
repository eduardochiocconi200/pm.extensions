import { Card, Col, Container, Row } from 'react-bootstrap';

function PhaseDetails({ phase })
{
    return (
        <>
            <Container style={{ margin: '25px'}}>
                <Card border="light">
                    <Card.Body>
                    <Card.Text style={{fontSize: 14}}><b>{phase.name} Details:</b><br></br>
                        <Container>
                        <Row>
                        <Col sm="10">{phase.statistics.summary}
                        </Col>
                        </Row>
                        </Container>
                    </Card.Text>
                    </Card.Body>
                </Card>
            </Container>
        </>
    );
}

export default PhaseDetails;