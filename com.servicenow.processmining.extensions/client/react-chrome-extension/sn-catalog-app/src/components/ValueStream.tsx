import { useState } from "react";
import { CardGroup, Card, Container } from 'react-bootstrap'

function ValueStream() {
  const [selected, setSelected] = useState('phase3');

  const showPhaseDetails = (phase: string) => {
    alert('Current: ' + selected + ', New: ' + phase);
    setSelected(phase);
  }
        
  return (
    <>
      <Container style={{ margin: '25px'}}>
        <CardGroup>
          <Card border="light" onClick={ () => showPhaseDetails('phase1') } style={{ cursor: "pointer" }}>
            <Card.Img variant="left" src="src/images/chevron.png"/>
            <Card.ImgOverlay>
            <Card.Body style={{ textAlign: 'center' }}>
              <Card.Title style={{}}>Phase 1</Card.Title>
              <Card.Text style={{ fontSize: 12 }}>Avg: 4 hrs 15 mins</Card.Text>
            </Card.Body>
            </Card.ImgOverlay>
          </Card>

          <Card border="light" onClick={ () => showPhaseDetails('phase2') } style={{ cursor: "pointer" }}>
            <Card.Img variant="left" src="src/images/chevron.png"/>
            <Card.ImgOverlay>
            <Card.Body style={{ textAlign: 'center' }}>
              <Card.Title>Phase 2</Card.Title>
              <Card.Text style={{ fontSize: 12 }}>Avg: 4 hrs 15 mins</Card.Text>
            </Card.Body>
            </Card.ImgOverlay>
          </Card>

          <Card border="light" onClick={ () => showPhaseDetails('phase3') } style={{ cursor: "pointer" }}>
            <Card.Img variant="left" src="src/images/chevron.png"/>
            <Card.ImgOverlay>
            <Card.Body style={{ textAlign: 'center' }}>
              <Card.Title>Phase 3</Card.Title>
              <Card.Text style={{ fontSize: 12 }}>Avg: 4 hrs 15 mins</Card.Text>
            </Card.Body>
            </Card.ImgOverlay>
          </Card>

          <Card border="light" onClick={ () => showPhaseDetails('phase4') } style={{ cursor: "pointer" }}>
            <Card.Img variant="left" src="src/images/chevron.png"/>
            <Card.ImgOverlay>
            <Card.Body style={{ textAlign: 'center' }}>
              <Card.Title>Phase 4</Card.Title>
              <Card.Text style={{ fontSize: 12 }}>Avg: 4 hrs 15 mins</Card.Text>
            </Card.Body>
            </Card.ImgOverlay>
          </Card>

          <Card border="light" onClick={ () => showPhaseDetails('phase5') } style={{ cursor: "pointer" }}>
            <Card.Img variant="left" src="src/images/chevron.png"/>
            <Card.ImgOverlay>
            <Card.Body style={{ textAlign: 'center' }}>
              <Card.Title>Phase 5</Card.Title>
              <Card.Text style={{ fontSize: 12 }}>Avg: 4 hrs 15 mins</Card.Text>
            </Card.Body>
            </Card.ImgOverlay>
          </Card>
        </CardGroup>
      </Container>

      <Container style={{ margin: '25px'}}>
        <Card border="light">
          <Card.Body>
            <Card.Text style={{fontSize: 12}}><b>Details:</b><br></br>
            - Reword: SCORE.<br></br>
            - Savings: SCORE.<br></br>
            - ROI: SCORE.<br></br>
            fsdfsdfsd sdfasf sdfsdf sfsfds sdfsdf
            </Card.Text>
          </Card.Body>
        </Card>
      </Container>
    </>
  );
}

export default ValueStream;