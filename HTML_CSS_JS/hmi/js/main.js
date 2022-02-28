REX.HMI.init = function () {
    //vzorkování 10x za sekundu - perioda 100 ms
    REX.HMI.setRefreshRate(100);

    // Rotace elementu dle stredu
    function rotateSpool(svgElArrow,svgElSpool, angle) {
        var cxSpool = parseFloat(svgElSpool.getAttribute("cx"));
        var cySpool = parseFloat(svgElSpool.getAttribute("cy"));
        // Rotace elementů podle středu
        svgElArrow.setAttributeNS(null, "transform", "rotate(" + angle  + ","+ cxSpool +"," + cySpool +")");
        svgElSpool.setAttributeNS(null, "transform", "rotate(" + angle + ","+ cxSpool +"," + cySpool +")");
    }
    
    function rotateBall(svgElement, angle) {
        var cxBall = parseFloat(svgElement.getAttribute("cx"));
        var cyBall = parseFloat(svgElement.getAttribute("cy"));
        // Rotace míče podle středu
        // (Poloměr špulky)/(Poloměr míče) = 160/140 = 8/7
        svgElement.setAttributeNS(null, "transform", "rotate(" + 8/6 *angle + ","+ cxBall +"," + cyBall +")");
    }

    // Posun míče po špulce
    function moveBall(svgElement, angle) {
        const r = 280;
        var oldX = 350;
        var oldY = 140;
        var newY = r-r*Math.cos(angle);
        var newX = r*Math.sin(angle);
        // Posun míče po trajektorii
        svgElement.setAttributeNS(null, "cx", +oldX + +newX);
        svgElement.setAttributeNS(null, "cy", +oldY + +newY);
    }
    // Zde se doplní registrace čtení hodnot s targetu
    REX.HMI.addItems([
        {alias:"Fi1", cstring:"mic.TRND:u1"},
        {alias:"dFi1", cstring:"mic.TRND:u2"},
        {alias:"Fi2", cstring:"mic.TRND:u3"},
        {alias:"dFi2", cstring:"mic.TRND:u4"},
        
        {alias:"nudge", cstring:"mic.MP_NUDGE:BSTATE",write:true},
        {alias:"gainNudge", cstring:"mic.GAIN_NUDGE:k",write:true},
        {alias:"disturbIntensity", cstring:"mic.SGI:amp",write:true},
        {alias:"reset", cstring:"mic.MP_INTEG_RST:BSTATE",write:true},
        {alias:"y0", cstring:"mic.CNR_y0:ycn",write:true}
    ]);

    let fi1Input = document.getElementById('Fi1');
    let arrowSVG = document.getElementById('arrow');
    let spoolSVG = document.getElementById('spool');
    REX.HMI.get('Fi1').on('change',function(itm){
        let value = itm.getValue();
        value = value.toFixed(3);
        fi1Input.innerHTML = (value* (180/Math.PI)).toFixed(2);

        rotateSpool(arrowSVG,spoolSVG, value * (180/Math.PI));
        

    });
    let dfi1Input = document.getElementById('dFi1');
    REX.HMI.get('dFi1').on('change',function(itm){
        let value = itm.getValue();
        value = value.toFixed(3);
        dfi1Input.innerHTML = (value* (180/Math.PI)).toFixed(2);
    });
    let fi2Input = document.getElementById('Fi2');
    let ballSVG = document.getElementById('ball');
    REX.HMI.get('Fi2').on('change',function(itm){
        let value = itm.getValue();
        value = value.toFixed(3);
        fi2Input.innerHTML = (value* (180/Math.PI)).toFixed(2);
        moveBall(ballSVG, value);
        
        rotateBall(ballSVG, -REX.HMI.get('Fi1').value * (180/Math.PI))
    });

    
    let nudgeGain = document.getElementById('nudges');
    nudgeInput = function () {
        let nudgeInt = Number(nudgeGain.value)
        REX.HMI.get('gainNudge').write(nudgeInt);
        REX.HMI.get('nudge').write(true);
    };

    
    let disturbIntSlider = document.getElementById('intensity');
    disturbIntSlider.addEventListener('change',function (event){
        let distInt;
        let distString;
        switch(Number(disturbIntSlider.value)) {
            case 0:
                distInt = 0
                distString = "VYPNUTO"
                break;
            case 1:
                distInt = 0.05
                distString = "NÍZKÁ"
              break;
            case 2:
                distInt = 0.1
                distString = "STŘEDNÍ"
              break;
            case 3:
                distInt = 0.2
                distString = "VYSOKÁ"
              break;
        }
        document.getElementById("intStr").innerHTML = distString;
        REX.HMI.get('disturbIntensity').write(distInt);
    }, false);

    resetInput = function () {
        REX.HMI.get('reset').write(true);
    };


    let y0Input = document.getElementById('y0');
    y0Input.addEventListener('change',function (event){
        let y0Value = Number(y0Input.value)* (Math.PI/180);
        let y0Min = Number(y0Input.min)* (Math.PI/180);
        let y0Max = Number(y0Input.max)* (Math.PI/180);
        if (y0Value >= y0Max){
            REX.HMI.get('y0').write(y0Max);
            y0Input.value = Number(y0Input.max);
        } else if (y0Value <= y0Min){
            REX.HMI.get('y0').write(y0Min);
            y0Input.value = Number(y0Input.min);
        } else {
            REX.HMI.get('y0').write(y0Value);
        }
    }, false);

};