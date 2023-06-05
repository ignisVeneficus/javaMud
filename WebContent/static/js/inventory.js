/**
 * 
 */

function putInventory(array){
	let div=$("#inventoryWrapper");
	div.empty();
	if((array!=null) && (array.length>0)){
		$.each(array, function( index, value ) {
			addInventoryItem(value,div);
		});
	}
	return parent;
}
function addInventoryItem(txt,node){
	let item=$("<div></div>").addClass("item");
	let name=$("<div></div>").addClass("name").appendTo(item).text(txt);
	let realName = txt.split(" ")
	let i = realName.length;
	$("<a></a>").addClass("iconButton icon-drop").appendTo(item).attr("data-tooltip","Eldob").on("click",function(){
		send("felvesz " + realName[i-1]);
	});
	$("<a></a>").addClass("iconButton icon-look").appendTo(item).attr("data-tooltip","Megvizsgál").on("click",function(){
		send("néz " + realName[i-1]+ " nalam");
	});
	item.appendTo(node);
	console.debug(node);
}