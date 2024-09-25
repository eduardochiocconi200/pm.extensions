import { useEffect, useState } from "react";
import axios from "axios";

interface FlowType {
  id: string,
  modelName: string,
  type : string,
  modelVersion: string,
  averageExecutionTime : string,
  successfulInstances : string,
  failedInstances : string
}

function syncAction(fType: string, fId: string) {
  var data = {}
  axios.post('http://localhost:8080/servicecatalog/' + fType + '/' + fId + '/sync', data, { headers: { "Access-Control-Allow-Origin" : "*"} })
    .then((data) => {
      console.log('Requested sync action: (' + data + ')');
    })
    .catch((err) => {
      console.log("AXIOS ERROR: ", err)
    })
}

function mineAction(fType: string, fId: string) {
  var data = {}
  axios.post('http://localhost:8080/servicecatalog/' + fType + '/' + fId + '/mine', data, { headers: { "Access-Control-Allow-Origin" : "*"} })
    .then((data) => {
      console.log('Requested mine action: (' + data + ')');
    })
    .catch((err) => {
      console.log("AXIOS ERROR: ", err)
    })
}

function Body() {
  function getServiceCatalogWorkflows() {
    axios.get(`http://localhost:8080/servicecatalog`, { headers: { "Access-Control-Allow-Origin" : "*"} })
      .then(response => response.data)
      .then((data) => {
        console.log('Retrieved Catalog workflows');
        const wflows = data;
        setWorkflows(wflows);
      });
  }  
  // Hooks
  const [selectedIndex, setSelectedIndex] = useState(-1);
  const [workflows, setWorkflows] = useState([]);

  // Function.
  const getMessage = () => {
      return workflows.length === 0 ? <p>No active workflows with an implementation available in the Service Catalog.</p> : null;
  }

  useEffect(() => {
    getServiceCatalogWorkflows();
  }, []);

  return (
    <>
      <div className="margin:100px">
      <h4>List of active Processes/Workflows deployed in your ServiceNow Instance</h4>
      {getMessage()}
      <table className="table table-sm table-bordered table-striped table-hover">
        <thead>
          <tr>
            <th scope="col" style={{ width : '30%', textAlign : "center", verticalAlign : "top" }}>Process/Workflow Name</th>
            <th scope="col" style={{ width :  '5%', textAlign : "center", verticalAlign : "top" }}>Type</th>
            <th scope="col" style={{ width : '15%', textAlign : "center", verticalAlign : "top" }}>Last Update</th>
            <th scope="col" style={{ width : '10%', textAlign : "center", verticalAlign : "top" }}>Avg Cycle Time</th>
            <th scope="col" style={{ width : '10%', textAlign : "center", verticalAlign : "top" }}>Successful Executions</th>
            <th scope="col" style={{ width : '10%', textAlign : "center", verticalAlign : "top" }}>Failed Executions</th>
            <th scope="col" style={{ width : '20%', textAlign : "center", verticalAlign : "top" }}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {workflows.map((workflow, index) => (
          <tr key={(workflow as FlowType).id} className={selectedIndex === index ? "table-active" : ""} onClick={() => setSelectedIndex(index)}>
            <td style={{ textAlign : "left" }}>{(workflow as FlowType).modelName}</td>
            <td style={{ textAlign : "center" }}>{(workflow as FlowType).type}</td>
            <td style={{ textAlign : "center" }}>{(workflow as FlowType).modelVersion}</td>
            <td style={{ textAlign : "right" }}>{(workflow as FlowType).averageExecutionTime}</td>
            <td style={{ textAlign : "right" }}>{(workflow as FlowType).successfulInstances}</td>
            <td style={{ textAlign : "right" }}>{(workflow as FlowType).failedInstances}</td>
            <td style={{ textAlign : "center" }}><div><a href="#" onClick={() => syncAction((workflow as FlowType).type, (workflow as FlowType).id)} className="btn btn-primary active">Sync</a><a href="#" onClick={() => mineAction((workflow as FlowType).type, (workflow as FlowType).id)} className="btn btn-default active">Mine</a></div></td>
          </tr>))}
        </tbody>
      </table>
      </div>
    </>
  );
}

export default Body;