import {getHashParams,setHashParams} from '/net/web/hashparams.js';

function $(id) {
  return document.getElementById(id);
}

function num(input) {
  return parseFloat(input.value);
}

function numberWithCommas(x) {
  return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

class Model {
  constructor() {
    this.N;
    this.Rs;
    this.Fp;
    this.Ne;
    this.Fl;
    this.Fi;
    this.Fc;
    this.L;
  }
  calculateDrakeNumber() {
    this.N = Math.round(
      this.Rs * this.Fp * this.Ne *
      this.Fl * this.Fi * this.Fc *
      this.L);
  }
}

class Form {
  constructor(model) {
    this.model = model;
    const f = document.forms.default;
    this.N = f.N;
    this.Rs = f.Rs;
    this.Fp = f.Fp;
    this.Ne = f.Ne;
    this.Fl = f.Fl;
    this.Fi = f.Fi;
    this.Fc = f.Fc;
    this.L = f.L;
    this.button = $('button');
    this.button.onclick = update;
  }
  toModel() {
    this.model.Rs = num(this.Rs);
    this.model.Fp = num(this.Fp);
    this.model.Ne = num(this.Ne);
    this.model.Fl = num(this.Fl);
    this.model.Fi = num(this.Fi);
    this.model.Fc = num(this.Fc);
    this.model.L = num(this.L);
  }
  fromModel(m) {
    this.N.value = numberWithCommas(this.model.N);
    this.Rs.value = this.model.Rs;
    this.Fp.value = this.model.Fp;
    this.Ne.value = this.model.Ne;
    this.Fl.value = this.model.Fl;
    this.Fi.value = this.model.Fi;
    this.Fc.value = this.model.Fc;
    this.L.value = this.model.L;
  }
}

const M = new Model();
let F;

function update() {
  F.toModel();
  M.calculateDrakeNumber();
  F.fromModel();
  setHashParams(M);
}

function init() {
  if (location.hash) {
    getHashParams(M);
  }
  F = new Form(M);
  F.fromModel(M);
  update();
  setHashParams(M)
}

init();
