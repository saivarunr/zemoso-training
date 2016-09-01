var React=require('react');
var Root=React.createClass({
	render:function(){
		return (<div><InputText></InputText></div>);
	}
});
var ViewArea=React.createClass({
	getInitialState:function(){
		return ({data:[]});
	},
	componentWillReceiveProps:function(propSet){
		this.setState({data:propSet.inputData});
	},
	render:function(){
		var selfReference=this;
		var outputBuffer=[];
		this.state.data.map(function(d,i){
			outputBuffer.push(<div key={i}><div>{d}</div></div>);
		});
		return (<div><div>{outputBuffer}</div></div>);	
	}
});
var InputText=React.createClass({
	getInitialState:function(){
		return ({data:[]});
	},
	dataWriter:function(){
		var inputData=this.refs.userdata.value;
		this.refs.userdata.value="";
		if(inputData!=""){
			console.log(inputData);
			var data=this.state.data;
			data.push(inputData);
			data.reverse();
			this.setState({data:data});
		}
	},
	deleteThisNode:function(input){
			var data=this.state.data;
			data.splice(input,1);
			this.setState({data:data});
	},
	render:function(){
		var thisSelf=this;
		return (<div>
					<div>
						<input type="text" ref="userdata"/><button onClick={this.dataWriter}>Done!</button>
					</div>
					<div>
						<ViewArea inputData={this.state.data}></ViewArea>
					</div>
				</div>)
	}
});
module.exports=Root;
//ReactDOM.render(<Root></Root>,document.getElementById("container"));