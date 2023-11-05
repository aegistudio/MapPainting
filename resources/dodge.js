/*******************************************************************************************************
 * DODGE - Dodge bullets shooting from the edge!
 *
 * DODGE is a MapPainting canvas script. Developed by aegistudio, as the first demo game of MapPainting.
 * 
 * Where playing in the of DODGE, the players control a cross, dodging bullets shooting towards it. 
 * Every time the player get shot by a bullet, his health decrease by 1. And the player get killed after 
 * consuming his all 20 healthes. The health regain by 1 every round.
 * 
 * The game get harder and harder as the time goes by, and the player will become less likely to survive.
 * Let's see who survives the longest!
 * 
 * Enjoy! :)
 *
 *******************************************************************************************************/

var MAX_HEALTH = 20.0;
var MAX_ENEMIES = 128;

function targetedRate() {
	return Math.min(0.0 + (c.totalTicks / 8192), 0.5);
}

var SPEED_MAX = 2;
var SPEED_MIN = 0.7;

var SIMPLE_PROBABILITY = 0.95;

var LASER_PROBABILITY = 0.05;
var LASER_CHARGE = 20;
var LASER_HOLD = 60;
var LASER_WIDTH = 1.0;

function spawnEnemy() {
	// Calculate source.
	var quotient = (Math.floor(c.totalTicks / 128)) % 4;
	var remainder = c.totalTicks % 128;
	var recX; var recY;

	if(quotient == 0) { recX = remainder; recY = 0; }
	else if(quotient == 1) { recX = 127; recY = remainder; }
	else if(quotient == 2) { recX = 127 - remainder; recY = 127; }
	else { recX = 0; recY = 127 - remainder; }

	var recVecX;	var recVecY;	var targeted = false;
	var speed = (SPEED_MAX - SPEED_MIN) * Math.random() + SPEED_MIN;
	if(Math.random() >= targetedRate()) {
		var angle = 2 * Math.PI * Math.random();
		recVecX = Math.cos(angle) * speed;
		recVecY = Math.sin(angle) * speed;
		targeted = false;
	}
	else {
		recVecX = c.curx - recX;	recVecY = c.cury - recY;
		var len = Math.sqrt(recVecX * recVecX + recVecY * recVecY);
		recVecX *= speed / len; 
		recVecY *= speed / len;
		targeted = true;
	}

	// Select enemy.
	var enemy;	var selector = Math.random();

	if(selector < SIMPLE_PROBABILITY && selector >= 0) {
		// Simple bullet.
		var colorValue = new java.awt.Color(
			(targeted? 0.5 : 0) + (0.5 * Math.random()),
			(targeted? 0.5 : 0) + (0.5 * Math.random()),
			(targeted? 0.5 : 0) + (0.5 * Math.random()));

		enemy = {
			x : recX, y : recY, vecx : recVecX, vecy : recVecY,
			color : colorValue,
			update : function() { this.x += this.vecx; this.y += this.vecy; }, 
			paint : function() { g.set(this.x, this.y, this.color); },
			outOfBound : function() {
				if(this.x < 0 || this.x >= 128) return true;
				if(this.y < 0 || this.y >= 128) return true;
				return false;
			},
			hit: function() {
				var dx = this.x - c.curx;
				var dy = this.y - c.cury;
				return (dx * dx + dy * dy) < 2.0;
			}
		};
	} else selector -= SIMPLE_PROBABILITY;

	if(selector < LASER_PROBABILITY && selector >= 0) {
		// Laser bullet.
		enemy = {
			x : recX, y : recY, vecx : recVecX / speed, vecy : recVecY / speed,
			chargeColor : new java.awt.Color(targeted? 0.5 : 0.0, 0.0, targeted? 0.0 : 0.5),
			holdColor : new java.awt.Color(targeted? 1.0 : 0.5, 0.5, targeted? 0.5 : 1.0), status: 0,
			update : function() { this.status ++; }, 
			paint : function() { 
				var px = this.x; var py = this.y;
				for(; px>=0 && px<=128 && py>=0 && py<=128; px+=this.vecx, py+=this.vecy) {
					if(this.status <= LASER_CHARGE) g.set(px, py, this.chargeColor);
					else g.set(px, py, this.holdColor);
				}
			},
			outOfBound : function() {
				return this.status > (LASER_CHARGE + LASER_HOLD);
			},
			hit: function() {
				if(this.status <= LASER_CHARGE) return false;
				A = this.vecy; B = -this.vecx; C = this.vecx * this.y - this.vecy * this.x;
				d = Math.abs(A * c.curx + B * c.cury + C) / Math.sqrt(A * A + B * B);
				return d <= LASER_WIDTH;
			}
		};
	} else selector -= LASER_PROBABILITY;

	if(enemy != undefined) c.enemy.push(enemy);
}

function onUpdate() {
	if(c.enemy.length < Math.min(MAX_ENEMIES, (c.totalTicks / MAX_HEALTH))) spawnEnemy();

	for(var i = 0; i < c.enemy.length; i ++)
		if(c.enemy[i] != undefined) {
			c.enemy[i].update();

			if(c.enemy[i].outOfBound())
				c.enemy[i] = undefined;
			else if(c.enemy[i].hit()) {
				c.health = Math.max(0, c.health - 1);
				c.enemy[i] = undefined;
			}
		}

	if(c.totalTicks % 512 == 0) c.health = Math.min(MAX_HEALTH, c.health + 1);
	c.totalTicks ++;
}

function onRepaint() {
	var healthPercent = c.health / MAX_HEALTH;
	g.clear(new java.awt.Color(1.0 - healthPercent, 0.0, 0.0));

	// Draw health bar.
	var green = new java.awt.Color(0.0, 1.0, 0.0);
	var red = new java.awt.Color(1.0, 0.0, 0.0);
	for(var off = 5; off <= 122; off ++) {
		var location = (off - 5.0) / 117;
		g.set(off, 125, green);
		g.set(off, 124, location < healthPercent? green : red);
		g.set(off, 123, location < healthPercent? green : red);
		g.set(off, 122, green);
	}
	g.set(4, 125, green);	g.set(123, 125, green);
	g.set(4, 124, green);	g.set(123, 124, green);
	g.set(4, 123, green);	g.set(123, 123, green);
	g.set(4, 122, green);	g.set(123, 122, green);


	// Draw player.
	var color = new java.awt.Color(1 - healthPercent, healthPercent, 0.0);
	var range = 5;
	var i = -range + 1;
	while(i < range) {
		g.set(c.curx + i, c.cury, color);
		g.set(c.curx, c.cury + i, color);
		i ++;
	}

	// Draw enemies.
	for(var i = 0; i < c.enemy.length; ) 
		if(c.enemy[i] != undefined) {
			c.enemy[i].paint();
			i ++;
		}
		else c.enemy.splice(i, 1);
	g.repaint();
}

function onTick() {
	onUpdate();
	onRepaint();
}

function onTap(x, y, who) {
	c.curx = x;
	c.cury = y;
}

function main() {
	g.clear(new java.awt.Color(0, 0, 0));
	i.hotspot(null, "onTap", 0, 0, 127, 127);
	i.tick(null, "onTick");

	c.totalTicks = 0;
	c.curx = 64;
	c.cury = 64;
	c.health = 20;
	c.enemy = new Array();
}

