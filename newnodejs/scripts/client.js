
var React=require('react');
var ReactDOM=require('react-dom');
var RootApp=React.createFactory(require('./../scripts/thisReact.js'));

ReactDOM.render(new RootApp({}),document.getElementById("container"));