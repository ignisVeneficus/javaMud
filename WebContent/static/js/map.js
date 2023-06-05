function putMap(map){
	var grid = 25;
	parent = $("#terkepContainer").empty();
	let minx = 1000;
	let maxx = 0;
	let miny = 1000;
	let maxy = 0;
	
	let youAreHere=null;
	let hasRoom=false;
	$.each(map.connections, function(index,line){
		let cline=$("<line></line>").attr("x1",""+((line.x1+0.5)*grid)+"").attr("y1",""+((line.y1+0.5)*grid)+"").
		attr("x2",""+((line.x2+0.5)*grid)+"").attr("y2",""+((line.y2+0.5)*grid)+"").addClass("roomConnection");
		cline.appendTo(parent);
	});
	$.each(map.items, function( index, item ) {
		/*
		minx = Math.min(minx,item.x);
		maxx = Math.max(maxx,item.x);
		miny = Math.min(miny,item.y);
		maxy = Math.max(maxy,item.y);
		*/
		let type=null;
		$.each(item.types, function(id,itype){
			itype = itype.replace("/","_");
			if(itype=="room"){
				hasRoom = true;
			}
			if((map.youAreHere)&&(item.id==map.youAreHere)){
				youAreHere = item;
			}
			if($("symbol#m_"+itype).length > 0 ){
				type=itype;
				return false;
			}
		});
		
		let node = $("<use/>");
		node.attr("xlink:href","#m_"+type).attr("width","25.5").attr("height","25.5").attr("x","" + (item.x*grid)+"").attr("y","" + (item.y*grid)+"").
		attr("id",item.id);
		if(type=="room"){
			if((!item.grp) || (item.grp=="")){
				node.addClass("white");
			}
			else{
				node.addClass("grp_" + item.grp);
			}
		}
		node.appendTo(parent);
		
		
		if(item.lines){
			$.each(item.lines, function(key,value){
				for(var i = 0;i<value.length;i++){
					let line=$("<line></line>").attr("x1",""+((item.x+0.5)*grid)+"").attr("y1",""+((item.y+0.5)*grid)+"");
					let str = value.substring(i,i+1);
					let x = 0;
					let y = 0;
					switch(str){
					case "1":
						y=-1;
						break;
					case "2":
						y=-1;
						x=1;
						break;
					case "3":
						x=1;
						break;
					case "4":
						y=1;
						x=1;
						break;
					case "5":
						y=1;
						break;
					case "6":
						y=1;
						x=-1;
						break;
					case "7":
						x=-1;
						break;
					case "8":
						x=-1;
						y=-1;
						break;
					}
					line.attr("x2",""+((item.x+0.5+x*0.5)*grid)+"").attr("y2",""+((item.y+0.5+y*0.5)*grid)+"");
					line.addClass(key);
					line.appendTo(parent);
				}
			});
		}
		$.each(item.overlays, function(id,itype){
			itype = itype.replace("/","_");
			if($("symbol#m_o_"+itype).length > 0 ){
				let over = $("<use/>");
				over.attr("xlink:href","#m_o_"+itype).attr("width","25.5").attr("height","25.5").attr("x","" + (item.x*grid)+"").attr("y","" + (item.y*grid)+"");
				over.appendTo(parent);
			}
		});

	});

	
	minx = 0;
	maxx = 25;
	miny = 0;
	maxy = 17;
	if(map.items){
		let svg=$("svg#terkep");
		svg.attr("viewBox","" +((minx)*grid)+" " +((miny)*grid) +" "+ +((maxx+1)*grid)+" " +((maxy+1)*grid) +"").
			attr("style","enable-background:new "+((minx)*grid)+" " +((miny)*grid) +" "+ +((maxx+1)*grid)+" " +((maxy+1)*grid) +";");
	}
	if(youAreHere){
		let node = $("<use/>");
		node.attr("xlink:href","#m_"+(hasRoom?"room":"outland")+"FocusOverlay").attr("width","25.5").attr("height","25.5").attr("x","" + (youAreHere.x*grid)+"").attr("y","" + (youAreHere.y*grid)+"");
		node.appendTo(parent);
	}
	parent.html(parent.html());
}