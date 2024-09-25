function getElementByXpath(path) {
    return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
}

function addStyle(styleString) {
    const style = document.createElement('style');
    style.textContent = styleString;
    document.head.append(style);
}
  
addStyle(`
.pm-ext-banner {
    background: #009579;
}
`);

addStyle(`
.pm-ext-banner-content {
    padding: 16px;
    max-width: 1000px;
    margin: 0 auto;
    display: flex;
    align-items: center;
}
`);

addStyle(`
.pm-ext-banner-text {
    flex-grow: 1;
    line-height: 1.4;
    font-family: 'Quicksand', sans-serif;
    align: center;
}
`);

addStyle(`
.pm-ext-banner-close {
    background: none;
    border: none;
    cursor: pointer;
}
`);

const app = document.createElement('div');
app.id = "pm-ext-banner";
const entryPoint = getElementByXpath("//span[@id='SEISMIC_ARIA_LIVE_REGION_ASSERTIVE']");
if (entryPoint) {
    var pmHeaderHTML = '<div class="pm-ext-banner-content">';
    pmHeaderHTML += '<div class="pm-ext-banner-text">';
    pmHeaderHTML += '<strong>The Process Mining Extension Plugin is installed - Analyze the performance of your Service Catalog Workflows:</strong><br>';
    pmHeaderHTML += '<a href="http:////localhost:5173" target="_blank">Click here to open the Service Catalog Optimization Dasboard.</a>';
    // pmHeaderHTML += '<a href="http:////localhost:8080" target="_blank">Click here to open the Service Catalog Optimization Dasboard.</a>';    
    pmHeaderHTML += '</div>';
    pmHeaderHTML += '</div>';
    app.innerHTML = pmHeaderHTML;

    document.body.insertBefore(app, document.body.children[0]);
}