import pkg from './package.json' assert { type: "json" };;

export default [
	{
		input: 'src/Main.mjs',
		output: {
			name: 'Sandbox',
			file: pkg.module,
			format: 'esm'
		}
	}
];
