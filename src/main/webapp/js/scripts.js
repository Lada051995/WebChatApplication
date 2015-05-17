var messageOption = function (name, text, value){
	return{
		user : name,
		message : text,
		id : value
	};
};

var fullName = function (name, surname){
	return{
		name : name,
		surname : surname
	};
};
var appState = {
	mainUrl : 'chat',
	token : 'TE0EN'
};
var listforSavingMessages = [];

function run(){
	var send = document.getElementById('send');
	var rename = document.getElementById('rename');
	var deletee = document.getElementById('delete');
	var inSelect = document.getElementById('select');
	var edit = document.getElementById('send_edit_message');
	var onOff = document.getElementById('OnOff');
	
	restoreMessages();
	
	if (restoreName() != null){
		var nameAndSurname = restoreName();
		createNameSurname(nameAndSurname);
	}
	
	send.addEventListener('click', EventSend);
	rename.addEventListener('click', EventRename);
	deletee.addEventListener('click', EventDelete);
	inSelect.addEventListener('click', EventActionSelect);
	edit.addEventListener('click', EventActionEdit);
	onOff.addEventListener('click', EventOnOff);
}

function EventOnOff(evtObj){
	var onoff = document.getElementById('OnOff');
	onoff.className = 'btn btn-success';
	onoff.value = "ON";
}

function EventSend(evtObj){
	var areatext = document.getElementById('textarea');
	var textArea = document.getElementById('name_surname');
	if(areatext.value&&textArea.value){
		var selected = document.getElementById('select');
		var option = document.createElement("option");
		option.text = textArea.value + ": " + areatext.value;
		option.value = select.length;
		select.add(option);
		
        var sendmessage = messageOption(textArea.value, areatext.value, select.length);
		areatext.value = "";
		storeMessages(sendmessage, function(){});
		}
}

function EventRename(evnObj){
	var myName = document.getElementById('name');
	var mySurname = document.getElementById('surname');
	var textArea = document.getElementById('name_surname');
	if(myName.value&&mySurname.value){
		storeName(fullName(myName.value, mySurname.value));
		textArea.value = myName.value + " " + mySurname.value;
		myName.value = "";
		mySurname.value = "";
	   
	}
}

function EventDelete(evnObj){
	var n_s = document.getElementById('name_surname');
	var mySurname = document.getElementById('surname');
	var edit_text = document.getElementById('textformessage');
	var index = document.getElementById('select').selectedIndex;
	var select = document.getElementById('select')[index];
	var subindex = select.text.indexOf(":");
	var subindex2 = select.text.indexOf('*');
	if (n_s.value == select.text.substring(0, subindex)&& select.text.substring(subindex+1, subindex2)!= "Deleted message"){
	     
	     var message = messageOption(n_s.value, select.text, index);
	     deleteMessages(message.id, function(){});
	     edit_text.value = "";
	}
}

function EventActionSelect(evnObj){
	var textarea = document.getElementById('textformessage');
	var index = document.getElementById('select').selectedIndex;
	var selected = document.getElementById('select')[index];
	var n_s = document.getElementById('name_surname');
	var subindex = selected.text.indexOf(":");
	var subindex2 = selected.text.length;
	if (selected.text.indexOf('*')!=(-1)){
	    	subindex2 = selected.text.indexOf('*');
	    }
	if( (n_s.value == selected.text.substring(0, subindex)) && (selected.text.substring(subindex+1, subindex2)!= "Deleted message")){
	     textarea.value = selected.text.substring(subindex+1, subindex2);
	}
}

function EventActionEdit(evnObj){
	var n_s = document.getElementById('name_surname');
	var mySurname = document.getElementById('surname');
	var edit_text = document.getElementById('textformessage');
	var index = document.getElementById('select').selectedIndex;
	var select = document.getElementById('select')[index];
    var subindex = select.text.indexOf(":");
	var subindex2 = select.text.length;
	if((select.text.substring(subindex+1, subindex2)!= "Deleted message") && (n_s.value == select.text.substring(0, subindex))){
	     
	     var mess = messageOption(n_s.value, edit_text.value+ " " + '*', index);
	     editMessages(mess, function(){});
	     edit_text.value = "";
	}
}

function storeMessages(message, continueWith){
	post(appState.mainUrl, JSON.stringify(message), function(){
		
	});
}

function editMessages(message, continueWith){
	put(appState.mainUrl, JSON.stringify(message), function() {
		
	});
}

function restoreMessages(continueWith){
	var url = appState.mainUrl + '?token=' + appState.token;
	
	var item;
	get(url, function(responseText) {
		console.assert(responseText != null);
        EventOnOff();
		appState.token = JSON.parse(responseText).token;
		var response = JSON.parse(responseText);
        item = response.messages;
		createAllMessages(item);
		continueWith && continueWith();
		
	});
	setTimeout(restoreMessages,1000);
	
}

function deleteMessages(index, continueWith){
	var indextoken = index;
	var url = appState.mainUrl + '?token=' + "TN" + indextoken.toString() + "EN";
	var item;
	deleted(url, function() {
			
	});
	
	
}

function createAllMessages(allMessages){
	for ( var i = 0; i < allMessages.length; i++){
		if(document.getElementById('select').childElementCount > allMessages[i].id) {
			var index = document.getElementById('select').selectedIndex;
	        var select = document.getElementById('select')[allMessages[i].id];
			if( select.text != allMessages[i].text) {
			select.text = allMessages[i].user + ":" + allMessages[i].message;
			}
		} else {
		addMessage(allMessages[i]);
		}
	}
}

function addMessage(message){
	var selected = document.getElementById('select');
	var option = document.createElement("option");
	option.text = message.user + ": " + message.message;
	option.value = message.id;
	
	selected.add(option);
}

function storeName(mystruct){
	if (typeof (Storage) == "undefined"){
		alert('local storage is not accessible');
		return;
	}
	localStorage.setItem("MyName", JSON.stringify(mystruct));
}

function restoreName(){
	if(typeof (Storage) == "undefined"){
		alert('local storage in not accessible');
		return;
	}
	var item = localStorage.getItem("MyName");
	return item && JSON.parse(item);
}

function createNameSurname(nameAndSurname){
	var textArea = document.getElementById('name_surname');
	textArea.value = nameAndSurname.name + " " + nameAndSurname.surname;
}



function defaultErrorHandler(message) {
	var onoff = document.getElementById('OnOff');
	    onoff.className = 'btn btn-danger';
	    onoff.value = "OFF";
}

function get(url, continueWith, continueWithError) {
	ajax('GET', url, null, continueWith, continueWithError);
}

function post(url, data, continueWith, continueWithError) {
	ajax('POST', url, data, continueWith, continueWithError);	
}

function put(url, data, continueWith, continueWithError) {
	ajax('PUT', url, data, continueWith, continueWithError);	
}

function deleted(url, data, continueWith, continueWithError) {
	ajax('DELETE', url, null, continueWith, continueWithError);	
}

function isError(text) {
	if(text == "")
		return false;
	
	try {
		var obj = JSON.parse(text);
	} catch(ex) {
		return true;
	}

	return !!obj.error;
}

function ajax(method, url, data, continueWith, continueWithError) {
	var xhr = new XMLHttpRequest();

	continueWithError = continueWithError || defaultErrorHandler;
	xhr.open(method || 'GET', url, true);

	xhr.onload = function () {
		if (xhr.readyState !== 4)
			return;

		if(xhr.status != 200) {
			continueWithError('Error on the server side, response ' + xhr.status);
			return;
		}

		if(isError(xhr.responseText)) {
			continueWithError('Error on the server side, response ' + xhr.responseText);
			return;
		}

		continueWith(xhr.responseText);
	};    

    xhr.ontimeout = function () {
    	ontinueWithError('Server timed out !');
    }

    xhr.onerror = function (e) {
    	var errMsg = 'Server connection error !\n'+
    	'\n' +
    	'Check if \n'+
    	'- server is active\n'+
    	'- server sends header "Access-Control-Allow-Origin:*"';

        continueWithError(errMsg);
    };

    xhr.send(data);
}

window.onerror = function(err) {
	output(err.toString());
}