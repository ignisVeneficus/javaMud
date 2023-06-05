/**
 * 
 */
function putRoomDescription(room){
	console.debug(room);
	writeIntoDiv(room.seeing,"latas");
	writeIntoDiv(room.listening,"hallas");
	writeIntoDiv(room.smelling,"szaglas");
	writeIntoDiv(room.magic,"magia");
	
	addLivings(room.livings);
	addItems(room.items);
	
	handleDirection(room.exits);
}
function handleDirection(exits){
	let wrapper = $('#exits');
	wrapper.empty();
	let windrose = $('#windrose');
	let links = windrose.find('a');
	links.each(function( index ) {
		disableRose($( this ));
	});
	for(var i=0;i<exits.length;i++){
		let exit=exits[i];
		switch(exit){
		case 'észak':
			enableRose('wr_eszak',exit);
			break;
		case 'északkelet':
			enableRose('wr_eszakkelet',exit);
			break;
		case 'kelet':
			enableRose('wr_kelet',exit);
			break;
		case 'délkelet':
			enableRose('wr_delkelet',exit);
			break;
		case 'dél':
			enableRose('wr_del',exit);
			break;
		case 'délnyugat':
			enableRose('wr_delnyugat',exit);
			break;
		case 'nyugat':
			enableRose('wr_nyugat',exit);
			break;
		case 'északnyugat':
			enableRose('wr_eszaknyugat',exit);
			break;
		default:
			addDirection(wrapper,exit);
			break;
		}
	}
	
}
function addDirection(parent,dir){
	let node = $("<a></a>").html(dir).addClass("button").on("click",function(){
		send(dir); }).appendTo(parent);
}
function disableRose(node){
	node.removeClass("enabled");
	node.off("click");
}
function enableRose(id,dir){
	let node = $('#'+id);
	node.addClass("enabled");
	node.on("click",function(){
		send(dir); });
}

function writeIntoDiv(array,parentID){
	var parent = $("#" + parentID);
	var div = parent.find(".wrapper");
	if((array!=null) && (array.length>0)){
		parent.show()
		div.empty();
		$.each(array, function( index, value ) {
			var node= $("<div></div>");
			if((value==null)||(value=="")){
				html = "&nbsp;";
			}
			else{
				html = value;
			}
			node.html(html);
			node.appendTo(div)
		});
	}
	else{
		parent.hide();
	}
	return parent;
}
function addLivings(array){
	let parent=$("#livings");
	var div = parent.find(".wrapper");
	div.empty();
	if((array!=null) && (array.length>0)){
		$.each(array, function( index, value ) {
			addLiving(value,div);
		});
	}
	return parent;
}
function addLiving(txt,node){
	let item=$("<div></div>").addClass("livingItem");
	let name=$("<div></div>").addClass("name").appendTo(item).text(txt);
	let realName = txt.split(" ")
	let i = realName.length;
	$("<a></a>").addClass("iconButton icon-kill").appendTo(item).attr("data-tooltip","Megtámad").on("click",function(){
		send("ől " + realName[i-1]);
	});
	$("<a></a>").addClass("iconButton icon-look").appendTo(item).attr("data-tooltip","Megvizsgál").on("click",function(){
		send("néz " + realName[i-1]);
	});
	item.appendTo(node);
	console.debug(node);
}

function addItems(array){
	let parent=$("#objects");
	var div = parent.find(".wrapper");
	div.empty();
	if((array!=null) && (array.length>0)){
		$.each(array, function( index, value ) {
			addItem(value,div);
		});
	}
	return parent;
}
function addItem(txt,node){
	let item=$("<div></div>").addClass("item");
	let name=$("<div></div>").addClass("name").appendTo(item).text(txt);
	let realName = txt.split(" ")
	let i = realName.length;
	$("<a></a>").addClass("iconButton icon-take").appendTo(item).attr("data-tooltip","Felvesz").on("click",function(){
		send("felvesz " + realName[i-1]);
	});
	$("<a></a>").addClass("iconButton icon-look").appendTo(item).attr("data-tooltip","Megvizsgál").on("click",function(){
		send("néz " + realName[i-1]);
	});
	item.appendTo(node);
	console.debug(node);
}