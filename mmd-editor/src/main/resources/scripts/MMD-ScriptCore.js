const Camera = require('./Camera');
const Player = require('./Player');

const Input = require('./engine/Input');
const InternalCalls = require('./engine/InternalCalls');
const KeyCode = require('./engine/KeyCode');
const Vector2 = require('./engine/Vector2');
const Vector3 = require('./engine/Vector3');

const Component = require('./engine/scene/components/Component');
const TransformComponent = require('./engine/scene/components/TransformComponent');
const Rigidbody2DComponent = require('./engine/scene/components/Rigidbody2DComponent');
const Entity = require('./engine/scene/Entity');

const classes = new Map();
classes.set('entity', ["Player", "Camera"]);