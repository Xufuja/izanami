import Camera from './Camera';
import Player from './Player';

import Input from './engine/Input';
import InternalCalls from './engine/InternalCalls';
import KeyCode from './engine/KeyCode';
import Vector2 from './engine/Vector2';
import Vector3 from './engine/Vector3';

import Component from './engine/scene/components/Component';
import TransformComponent from './engine/scene/components/TransformComponent';
import Rigidbody2DComponent from './engine/scene/components/Rigidbody2DComponent';
import Entity from './engine/scene/Entity';

const classes = new Map();
classes.set('entity', ["Player", "Camera"]);

export {
  Camera,
  Player,
  Input,
  InternalCalls,
  KeyCode,
  Vector2,
  Vector3,
  Component,
  TransformComponent,
  Rigidbody2DComponent,
  Entity,
  classes
};
