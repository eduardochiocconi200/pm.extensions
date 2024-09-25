import { useEffect, useState } from "react";
import Accordion from 'react-bootstrap/Accordion';
import ListGroup from 'react-bootstrap/ListGroup';
import Button from 'react-bootstrap/Button';
import axios from "axios";

interface FilterType {
  id: string,
  name: string,
  condition: string,
  caseFrequency: number,
  variantCount: number,
  totalDuration: number,
  avgDuration: number,
  medianDuration: number,
  stdDeviation: number,
  maxDuration: number,
  minDuration: number
}

function getTimeInMinutes(time: number)
{
  return Math.round(time/60);
}

function getTimeInHours(time: number)
{
  return Math.round(time/60/60);
}

function PMProjectBody()
{
  function getProcessMiningModels() {
    const url = 'http://localhost:8080/models';
    setModels([]);
    axios.get(url, { headers: { "Access-Control-Allow-Origin" : "*"} })
      .then(response => response.data)
      .then((data) => {
        console.log('Retrieved Process Mining Models');
        const models = data;
        setModels(models);
      })
      .catch((err) => {
        console.log("AXIOS ERROR: ", err)
      })
  };

  function getProcessMiningModelFilters(model : string) {
    setSelectedModel(model);
    setModelFilters([]);
    const url = 'http://localhost:8080/models/' + model + '/filters';
    axios.get(url, { headers: { "Access-Control-Allow-Origin" : "*"} })
      .then(response => response.data)
      .then((data) => {
        console.log('Retrieved Filters for Process Mining Model: ' + model);
        const filters = data;
        setModelFilters(filters);
      })
      .catch((err) => {
        console.log("AXIOS ERROR: ", err)
      })
  };

  const requestPowerPointDeckForFilter = (model: string, filter: string) => {
    const url = 'http://localhost:8080/models/' + model + '/filters/' + filter + '/ppt';
    axios.get(url, { responseType: "blob", headers: { "Access-Control-Allow-Origin" : "*"} })
      .then(response => response.data)
      .then((data) => {
        console.log('Retrieved Power Point Deck Process Mining Model: ' + model + ' and Filter: ' + filter + '.');
        // Create a temporary URL for the Blob
        const url = window.URL.createObjectURL(data);
        // Create a temporary <a> element to trigger the download
        const tempLink = document.createElement("a");
        tempLink.href = url;
        var currentdate = new Date();
        var datetime = currentdate.getDate() + "-"
                + (currentdate.getMonth()+1)  + "-"
                + currentdate.getFullYear() + " "
                + currentdate.getHours() + "-"
                + currentdate.getMinutes() + "-"
                + currentdate.getSeconds();
        tempLink.setAttribute(
          "download",
          filter + " Analysis - " + datetime + ".pptx"
        ); // Set the desired filename for the downloaded file

        // Append the <a> element to the body and click it to trigger the download
        document.body.appendChild(tempLink);
        tempLink.click();

        // Clean up the temporary elements and URL
        document.body.removeChild(tempLink);
        window.URL.revokeObjectURL(url);
      })
      .catch((err) => {
        console.log("AXIOS ERROR: ", err)
      })
  };

  const requestExcelCycletimeForFilter = (model: string, filter: string) => {
    const url = 'http://localhost:8080/models/' + model + '/filters/' + filter + '/xls';
    axios.get(url, { responseType: "blob", headers: { "Access-Control-Allow-Origin" : "*"} })
      .then(response => response.data)
      .then((data) => {
        console.log('Retrieved Excel for Process Mining Model: ' + model + ' and Filter: ' + filter + '.');
        // Create a temporary URL for the Blob
        const url = window.URL.createObjectURL(data);
        // Create a temporary <a> element to trigger the download
        const tempLink = document.createElement("a");
        tempLink.href = url;
        var currentdate = new Date();
        var datetime = currentdate.getDate() + "-"
                + (currentdate.getMonth()+1)  + "-"
                + currentdate.getFullYear() + " "
                + currentdate.getHours() + "-"
                + currentdate.getMinutes() + "-"
                + currentdate.getSeconds();
        tempLink.setAttribute(
          "download",
          filter + " Analysis - " + datetime + ".xlsx"
        ); // Set the desired filename for the downloaded file

        // Append the <a> element to the body and click it to trigger the download
        document.body.appendChild(tempLink);
        tempLink.click();

        // Clean up the temporary elements and URL
        document.body.removeChild(tempLink);
        window.URL.revokeObjectURL(url);
      })
      .catch((err) => {
        console.log("AXIOS ERROR: ", err)
      })
  };

  const requestBPMNDiagramsForFilter = (model: string, filter: string) => {
    const url = 'http://localhost:8080/models/' + model + '/filters/' + filter + '/bpmn';
    axios.get(url, { responseType: "blob", headers: { "Access-Control-Allow-Origin" : "*"} })
      .then(response => response.data)
      .then((data) => {
        console.log('Retrieved BPMN for Model: ' + model + ' and Filter: ' + filter + '.');
        // Create a temporary URL for the Blob
        const url = window.URL.createObjectURL(data);
        // Create a temporary <a> element to trigger the download
        const tempLink = document.createElement("a");
        tempLink.href = url;
        var currentdate = new Date();
        var datetime = currentdate.getDate() + "-"
                + (currentdate.getMonth()+1)  + "-"
                + currentdate.getFullYear() + " "
                + currentdate.getHours() + "-"
                + currentdate.getMinutes() + "-"
                + currentdate.getSeconds();
        tempLink.setAttribute(
          "download",
          filter + " Analysis - " + datetime + ".bpmn"
        ); // Set the desired filename for the downloaded file

        // Append the <a> element to the body and click it to trigger the download
        document.body.appendChild(tempLink);
        tempLink.click();

        // Clean up the temporary elements and URL
        document.body.removeChild(tempLink);
        window.URL.revokeObjectURL(url);
      })
      .catch((err) => {
        console.log("AXIOS ERROR: ", err)
      })
  };

  // Hooks
  const [selectedModel, setSelectedModel] = useState("");
  const [models, setModels] = useState([]);
  const [modelFilters, setModelFilters] = useState([]);

  // Function.
  const getMessage = () => {
      return models.length === 0 ? <p>No active Process Mining Projects available in your instance.</p> : <p>Select a Process Mining Project from the drop down list below</p>;
  }

  useEffect(() => {
    getProcessMiningModels();
  }, []);

  return (
    <>
      <div className="margin:100px">
        {getMessage()}
        <div>
          <select className="form-select" aria-label="Process Mining Project" onChange={(e) => getProcessMiningModelFilters(e.target.value)}>
            {models.length > 0 && <option value="Need to select a Process Mining Project" defaultValue="Need to select a Process Mining Project">Need to select a Process Mining Project</option>}
            {models.length > 0 && models.map((_index, value) =>(
              <option value={models[value]["pk"]["sysId"]}>{"Name: " + models[value]["name"] + " - Records: " + models[value]["totalRecords"] + " - Last Mined: " + models[value]["lastMinedTime"]}</option>
            ))}
          </select>
        </div>
        <div style={{marginTop: '20px'}}></div>
        {modelFilters.length > 0 && <h4>Process Mining Project Details</h4>}
        <div id="mainProcessStats">
        {modelFilters.length > 0 && modelFilters.slice(0,1).map((value) => (
            <ListGroup>
              <ListGroup.Item><b>Case Count: </b>{(value as FilterType).caseFrequency}</ListGroup.Item>
              <ListGroup.Item><b>Variant Count (Routes): </b>{(value as FilterType).variantCount}</ListGroup.Item>
              <ListGroup.Item><b>Case Average Cycle Time: </b>{getTimeInMinutes((value as FilterType).avgDuration) + " minutes (" + getTimeInHours((value as FilterType).avgDuration) + " hours)"}</ListGroup.Item>
              <ListGroup.Item><b>Case Median Cycle Time: </b>{getTimeInMinutes((value as FilterType).medianDuration) + " minutes (" + getTimeInHours((value as FilterType).medianDuration) + " hours)"}</ListGroup.Item>
              <ListGroup.Item><b>Case Minimum Cycle Time: </b>{getTimeInMinutes((value as FilterType).minDuration) + " minutes (" + getTimeInHours((value as FilterType).minDuration) + " hours)"}</ListGroup.Item>
              <ListGroup.Item><b>Case Maximum Cycle Time: </b>{getTimeInMinutes((value as FilterType).maxDuration) + " minutes (" + getTimeInHours((value as FilterType).maxDuration) + " hours)"}</ListGroup.Item>
              <ListGroup.Item><b>Case Total Time: </b>{getTimeInMinutes((value as FilterType).totalDuration) + " minutes (" + getTimeInHours((value as FilterType).totalDuration) + " hours)"}</ListGroup.Item>
            </ListGroup>
        ))}
        </div>
        <div style={{marginTop: '20px'}}></div>
        {modelFilters.length > 0 && <h4>Process Mining Project Filters</h4>}
        <div style={{marginTop: '15px'}}>
          <div className="accordion" id="accordionFilters">
            {modelFilters.length > 0 && modelFilters.slice(0).map((value, index) => (
            <Accordion>
              <Accordion.Item eventKey={"" + index + ""}>
                <Accordion.Header>Filter Name: {(value as FilterType).name}</Accordion.Header>
                <Accordion.Body>
                  <b>Name:</b> {(value as FilterType).name}<br></br>
                  <b>Total Records: </b> {(value as FilterType).caseFrequency}<br></br>
                  <b>Routes: </b> {(value as FilterType).variantCount}<br></br>
                  <b>Average Cycle Time: </b>{getTimeInMinutes((value as FilterType).avgDuration) + " minutes (" + getTimeInHours((value as FilterType).avgDuration) + " hours)"}<br></br>
                  <b>Median Cycle Time: </b>{getTimeInMinutes((value as FilterType).medianDuration) + " minutes (" + getTimeInHours((value as FilterType).medianDuration) + " hours)"}<br></br>
                  <b>Condition:</b> {(value as FilterType).condition}
                  <br></br><br></br>
                  <div id="Download Assets">
                    <Button onClick={ ()=> { requestPowerPointDeckForFilter(selectedModel, (value as FilterType).name)}} variant="primary">Download Filter Analysis PPT</Button>
                    {' '}
                    <Button onClick={ ()=> { requestExcelCycletimeForFilter(selectedModel, (value as FilterType).name)}} variant="primary">Download Cycle Time Excel</Button>
                    {' '}
                    <Button onClick={ ()=> { requestBPMNDiagramsForFilter(selectedModel, (value as FilterType).name)}} variant="primary">Download Variant BPMNs</Button>
                  </div>
                </Accordion.Body>
              </Accordion.Item>
            </Accordion>
            ))}
          </div>
        </div>
      </div>
      <div style={{marginTop: '20px'}}></div>
    </>
  );
}

export default PMProjectBody;
