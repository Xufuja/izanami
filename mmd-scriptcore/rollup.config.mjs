import pkg from './package.json' assert { type: "json" };;

export default [
	{
		input: 'src/Main.mjs',
		output: {
			name: 'MMD-ScriptCore',
			file: pkg.module,
			format: 'esm'
		}
	}
];
